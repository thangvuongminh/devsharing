package com.studyhard.application.service.impl;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import com.studyhard.application.config.PresendSupportCustomerEvaluator;
import com.studyhard.application.config.TranslationsPrePrompt;
import com.studyhard.application.dto.request.ChatMessageRequest;
import com.studyhard.application.dto.response.SupportTicketResponse;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.UserRole;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mapper.SupportTicketMapper;
import com.studyhard.application.model.RoleEnum;
import com.studyhard.application.model.SupportTicketStatus;
import com.studyhard.application.mongo.entity.ChatMessage;
import com.studyhard.application.mongo.entity.MessageType;
import com.studyhard.application.mongo.entity.SupportTicket;
import com.studyhard.application.mongo.repository.SupportTicketRepository;
import com.studyhard.application.repository.UserRoleRepository;
import com.studyhard.application.service.SupportTicketService;
import com.studyhard.application.utils.UserExtractor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.hibernate.sql.ast.tree.expression.QueryTransformer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Log4j2
public class SupportCustomerServiceImpl implements SupportTicketService {

  SimpMessagingTemplate messagingTemplate;
  SupportTicketRepository supportTicketRepository;
  SupportTicketMapper supportTicketMapper;
  UserRoleRepository userRoleRepository;
  VectorStore vectorStore;
  ChatClient chatClient;
  RedisTemplate<String, Object> redisTemplate;
  ChatClient.Builder chatClientBuilder;
  @NonFinal
  @Autowired
  @Qualifier("evaluationUserMessages")
  ChatClient evaluationUserMessages;
  @NonFinal
  @Value("classpath:ai/supportCustomerTicket.st")
  Resource systemPrompt;

  @Override
  public SupportTicketResponse createSupportTicket(ChatMessageRequest request) {
    Long userId = UserExtractor.getUserId();
    if (checkExistSupportTicket(userId)) {
      throw new StudyHardException(ExceptionEnum.SUPPORT_TICKET_IS_RESOLVED);
    }
    ChatMessage chatMessage = ChatMessage.builder()
        .type(MessageType.USER_CHAT)
        .content(request.getContent())
        .userId(userId)
        .sentAt(Instant.now())
        .build();
    List<ChatMessage> chatMessages = new ArrayList<>();
    chatMessages.add(chatMessage);
    SupportTicket supportTicket = SupportTicket.builder()
        .createdAt(Instant.now())
        .status(SupportTicketStatus.AI_PENDING_RESOLVE)
        .userId(userId)
        .messages(chatMessages)
        .build();
    supportTicketRepository.save(supportTicket);
    return supportTicketMapper.toSupportTicketResponse(supportTicket);
  }

  public Boolean checkExistSupportTicket(Long userId) {
    SupportTicket supportTicket = supportTicketRepository.findByUserIdAndStatusNotIn(userId,
        List.of(SupportTicketStatus.RESOLVED));
    if (supportTicket != null) {
      return true;
    }
    return false;
  }

  @Override
  public List<SupportTicketResponse> getAllSupportTicket() {
    long userId = UserExtractor.getUserId();
    List<SupportTicket> supportTickets = supportTicketRepository.findByUserIdOrderByCreatedAtDesc(
        userId);
    return supportTickets.stream().map(supportTicketMapper::toSupportTicketResponse).toList();
  }

  @Override
  public SupportTicketResponse getSupportTicketById(String ticketId) {
    Long userId = UserExtractor.getUserId();
    List<UserRole> userRole = userRoleRepository.findByUserId(userId);
    SupportTicket supportTicket = supportTicketRepository.findById(ticketId)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.SUPPORT_TICKET_NOT_FOND));
    for (UserRole ur : userRole) {
      if (ur.getRole().getRoleName().equals(RoleEnum.MODERATOR)) {
        return supportTicketMapper.toSupportTicketResponse(supportTicket);
      }
    }
    if (!supportTicket.getUserId().equals(userId)) {
      throw new StudyHardException(ExceptionEnum.SUPPORT_TICKET_NOT_FOND);
    }
    return supportTicketMapper.toSupportTicketResponse(supportTicket);
  }

  @Override
  @Transactional
  //important
  public void chatSupport(ChatMessageRequest request, String ticketId, Long userId) {
    log.info(ticketId);
    SupportTicket supportTicket = supportTicketRepository.findById(ticketId)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.SUPPORT_TICKET_NOT_FOND));
    Long moderatorId = supportTicket.getHandleByUserId();
    Boolean isModerator = false;
    if (moderatorId != null && moderatorId.equals(userId)) {
      isModerator = true;
    }
    ChatMessage chatMessage = ChatMessage.builder()
        .content(request.getContent())
        .sentAt(Instant.now())
        .type(isModerator ? MessageType.MODERATOR : MessageType.USER_CHAT)
        .userId(userId)
        .build();
    List<ChatMessage> chatMessages = supportTicket.getMessages();
    chatMessages.add(chatMessage);
    if (supportTicket.getStatus().equals(SupportTicketStatus.AI_PENDING_RESOLVE)) {

      /// /// CHECK  REQUEST FROM USER
      Boolean checkRequestModerator = checkRequestModerator(chatMessage, supportTicket);
      if (!checkRequestModerator) {
        String aiAnswer = callChatGptSupportCustomer(chatMessage);
        messagingTemplate.convertAndSend("/queue/support/" + ticketId, Map.of(
            "content", aiAnswer,
            "type", MessageType.AI_ASSISTANT.name()
        ));
        ChatMessage chatMessageAI = ChatMessage.builder()
            .content(aiAnswer)
            .sentAt(Instant.now())
            .type(MessageType.AI_ASSISTANT)
            .userId(-1L)
            .build();
        chatMessages.add(chatMessageAI);
        supportTicket.setMessages(chatMessages);
        supportTicketRepository.save(supportTicket);
        return;
      }
    } else {
      messagingTemplate.convertAndSend("/queue/support/" + ticketId, Map.of(
          "content", chatMessage.getContent(),
          "type", chatMessage.getType().name()
      ));
    }
  }

  public String callChatGptSupportCustomer(ChatMessage chatMessage) {
    TranslationsPrePrompt translationsPrePrompt = new TranslationsPrePrompt(chatClientBuilder);
    String query = translationsPrePrompt.translate(chatMessage.getContent(),"english");
    log.info(chatMessage.getContent());
    SearchRequest searchRequest = SearchRequest.builder()
        .query(query)
        .similarityThreshold(0.6)
        .topK(3)
        .build();
    List<Document> documentList = vectorStore.similaritySearch(searchRequest);
    List<String> docs = documentList.stream().map(Document::getText).toList();

    return chatClient.prompt()
        .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, chatMessage.getUserId()))
        .system(promptSystemSpec ->
            promptSystemSpec.text(systemPrompt).param("document", docs))
        .user(chatMessage.getContent())
        .call().content();
  }

  public String getHistoryChat(SupportTicket supportTicket) {
    LinkedHashSet<ChatMessage> chatMessages = new LinkedHashSet<>();
    StringBuilder allHistory = new StringBuilder();
    for (ChatMessage chatMessage : supportTicket.getMessages()) {
      allHistory.append(chatMessage.toString());
      allHistory.append("\n");
    }
    return allHistory.toString();
  }

  @Override
  public Boolean checkRequestModerator(ChatMessage chatMessage, SupportTicket supportTicket) {
    String historyChat = getHistoryChat(supportTicket);
    PresendSupportCustomerEvaluator evaluator = new PresendSupportCustomerEvaluator(
        evaluationUserMessages,
        historyChat);
    EvaluationResponse evaluationResponse = evaluator.evaluate(
        new EvaluationRequest(chatMessage.getContent(), null, null));
    if (evaluationResponse.isPass()) {
      supportTicket.setStatus(SupportTicketStatus.WAITING_MODERATOR);
      supportTicketRepository.save(supportTicket);
      messagingTemplate.convertAndSend("/queue/support/" + supportTicket.getId(),
          Map.of(
              "content", "Đang tìm người hỗ trợ, vui lòng chờ...",
              "senderType", "SYSTEM"
          )
      );
      Long moderatorId = findModeratorLoadBalancing();
      messagingTemplate.convertAndSendToUser(moderatorId.toString(),
          "/queue/support",
          Map.of(
              "ticketId", supportTicket.getId(),
              "content", "Có người đang cần hỗ trợ",
              "senderType", "SYSTEM"
          )
      );
      return true;
    }
    ;
    return false;
  }

  @Override
  public void moderatorJoin(String ticketId, Long userId) {
    System.out.println("DEBUG: Bắt đầu lưu Ticket với Mod ID: " + userId);
    Boolean checkRoleModerator = false;
    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
    for (UserRole ur : userRoles) {
      if (ur.getRole().getRoleName().equals(RoleEnum.MODERATOR)) {
        checkRoleModerator = true;
      }
    }
    if (!checkRoleModerator) {
      throw new StudyHardException(ExceptionEnum.SUPPORT_TICKET_NOT_FOND);
    }
    SupportTicket supportTicket = supportTicketRepository.findById(ticketId)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.SUPPORT_TICKET_NOT_FOND));
    if (!supportTicket.getStatus().equals(SupportTicketStatus.AI_PENDING_RESOLVE)
        && !supportTicket.getStatus().equals(SupportTicketStatus.WAITING_MODERATOR)) {
      throw new StudyHardException(ExceptionEnum.SUPPORT_TICKET_IS_RESOLVED);
    }
    System.out.println("DEBUG: Bắt đầu lưu Ticket với Mod ID2: " + userId);
    supportTicket.setStatus(SupportTicketStatus.PROCESSING);
    supportTicket.setHandleByUserId(userId);
    supportTicketRepository.save(supportTicket);
    messagingTemplate.convertAndSendToUser(supportTicket.getUserId().toString(),
        "/queue/support/" + ticketId,
        Map.of(
            "content", "Có người đang cần hỗ trợ",
            "senderType", "SYSTEM"
        )
    );
  }

  public Long findModeratorLoadBalancing() {
    Object modId = redisTemplate.opsForList()
        .rightPopAndLeftPush("moderator_is_online", "moderator_is_online");
    if (modId == null) {
      throw new StudyHardException(ExceptionEnum.SUPPORT_TICKET_IS_RESOLVED);
    }

    return Long.valueOf(modId.toString());
  }
  public void conservationChat(){

  }
}
