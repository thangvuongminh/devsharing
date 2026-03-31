package com.studyhard.application.service.impl;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import com.studyhard.application.dto.request.SupportTicketRequest;
import com.studyhard.application.dto.response.SupportTicketResponse;
import com.studyhard.application.entity.UserRole;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mapper.SupportTicketMapper;
import com.studyhard.application.model.RoleEnum;
import com.studyhard.application.model.SupportTicketStatus;
import com.studyhard.application.mongo.entity.FeedbackMessageTicket;
import com.studyhard.application.mongo.entity.SupportTicket;
import com.studyhard.application.mongo.repository.SupportTicketRepository;
import com.studyhard.application.repository.UserRoleRepository;
import com.studyhard.application.service.SupportTicketService;
import com.studyhard.application.utils.UserExtractor;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
@RequiredArgsConstructor
@Service
public class SupportCustomerServiceImpl implements SupportTicketService {
  SupportTicketRepository supportTicketRepository;
  SupportTicketMapper supportTicketMapper;
  UserRoleRepository userRoleRepository;
  ChatClient chatClient;
  VectorStore vectorStore;
  @NonFinal
  @Value("classpath:ai/supportCustomerTicket.st")
  Resource supportCustomerTicket;
  @Override
  public String createSupportTicket(SupportTicketRequest request) {
    Long userId=UserExtractor.getUserId();
    SearchRequest searchRequest=SearchRequest.builder()
        .query(request.getSubject())
        .similarityThreshold(0.6)
        .topK(3)
        .build();
    List<Document> documentList=vectorStore.similaritySearch(searchRequest);
    String searchAns=documentList.stream().map(Document::getText).collect(Collectors.joining("\n"));
    String response = chatClient.prompt()
        .advisors(advisorSpec -> advisorSpec
            .param(CONVERSATION_ID, userId))
        .system(s -> s.text(supportCustomerTicket)
            .param("document", searchAns)) // Kết quả từ Vector Store
        .user(request.getSubject())
        .toolContext(Map.of("userId", userId)) // Cực kỳ quan trọng để Tool biết ai đang gọi
        .call()
        .content();
    return response;
  }
  @Override
  public String feedBackSupportTicket(String ticketId, String message) {
    Long userId = UserExtractor.getUserId();
    List<UserRole> userRole=userRoleRepository.findByUserId(userId);
    SupportTicket supportTicket=supportTicketRepository.findById(ticketId).orElseThrow(() -> new StudyHardException(
        ExceptionEnum.SUPPORT_TICKET_NOT_FOND));
    List<FeedbackMessageTicket> messageAleadry=supportTicket.getMessages();
    Boolean isModerator=false;
    for(UserRole ur:userRole){
      if(ur.getRole().getRoleName().equals(RoleEnum.MODERATOR)){
        isModerator=true;
        break;
      }
    }
    if(isModerator){
        messageAleadry.add(FeedbackMessageTicket.builder()
                .role(RoleEnum.MODERATOR)
                .sentAt(Instant.now())
                .content(message)
            .build());
        supportTicket.setMessages(messageAleadry);
        supportTicketRepository.save(supportTicket);
    }
    RoleEnum eliminate=messageAleadry.get(messageAleadry.size()-1).getRole();
    for(UserRole ur:userRole){
      if(ur.getRole().getRoleName().equals(eliminate)){
        throw new StudyHardException(ExceptionEnum.SUPPORT_TICKET_IS_WAITING_RESPONSE);
      }
    }
    messageAleadry.add(FeedbackMessageTicket.builder()
        .role(userRole.get(0).getRole().getRoleName())
        .sentAt(Instant.now())
        .content(message)
        .build());
    supportTicket.setMessages(messageAleadry);
    supportTicketRepository.save(supportTicket);
    return  "Send successfully";
  }

  @Override
  public List<SupportTicketResponse> getAllSupportTicket() {
    long userId = UserExtractor.getUserId();
    List<SupportTicket> supportTickets = supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId);
    return supportTickets.stream().map(supportTicketMapper::toSupportTicketResponse).toList();
  }
}
