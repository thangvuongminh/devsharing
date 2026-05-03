package com.studyhard.application.service.impl;

import com.studyhard.application.dto.BlockDto;
import com.studyhard.application.dto.ContentDto;
import com.studyhard.application.dto.ContentSummaryDto;
import com.studyhard.application.dto.request.ContentReviewRequest;
import com.studyhard.application.dto.request.ContentSearchRequest;
import com.studyhard.application.dto.request.CreateContentRequest;
import com.studyhard.application.dto.response.ContentReviewResponse;
import com.studyhard.application.entity.Block;
import com.studyhard.application.entity.Category;
import com.studyhard.application.entity.Content;
import com.studyhard.application.entity.ContentReview;
import com.studyhard.application.entity.PurchaseContent;
import com.studyhard.application.entity.Transaction;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.Wallet;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mapper.ContentMapper;
import com.studyhard.application.model.ContentStatus;
import com.studyhard.application.model.ReviewAction;
import com.studyhard.application.model.TransactionType;
import com.studyhard.application.model.TypeFile;
import com.studyhard.application.redis.CartContent;
import com.studyhard.application.redis.repository.CardContentRepository;
import com.studyhard.application.repository.CategoryRepository;
import com.studyhard.application.repository.ContentRepository;
import com.studyhard.application.repository.ContentReviewRepository;
import com.studyhard.application.repository.PurchaseContentRepository;
import com.studyhard.application.repository.TransactionRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.WalletRepository;
import com.studyhard.application.service.CategoryService;
import com.studyhard.application.service.ContentService;
import com.studyhard.application.service.FileStorageService;
import com.studyhard.application.service.WalletService;
import com.studyhard.application.specification.ContentPreSpecification;
import com.studyhard.application.utils.UserExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.plaf.PanelUI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentServiceImpl implements ContentService {

  private static final Logger log = LoggerFactory.getLogger(ContentServiceImpl.class);
  ContentMapper contentMapper;
  ContentRepository contentRepository;
  CategoryRepository categoryRepository;
  CategoryService categoryService;
  CardContentRepository cardContentRepository;
  ContentReviewRepository contentReviewRepository;
  UserRepository userRepository;
  CloudinaryServiceImpl fileStorageService;
  WalletService walletService;
  PurchaseContentRepository purchaseContentRepository;
  TransactionRepository transactionRepository;
  @Override
  @Transactional
  public ContentDto createContent(CreateContentRequest createContentRequest) {
    Category category = categoryService.getCategoryById(createContentRequest.getCategoryId());
    User user = userRepository.findById(UserExtractor.getUserId()).get();
    List<String> public_ips = fileStorageService.saveFile(List.of(createContentRequest.getThumb()),
        TypeFile.CONTENT);
    List<String> thumbs = fileStorageService.getImage(public_ips, false);
    Content content = Content.builder()
        .creator(user)
        .description(createContentRequest.getDescription())
        .price(createContentRequest.getPrice().divide(WalletServiceImpl.VND_TO_CREDIT_RATE, 0,
            RoundingMode.HALF_UP))
        .title(createContentRequest.getTitle())
        .category(category)
        .level(createContentRequest.getContentLevel())
        .thumb(thumbs.get(0))
        .status(ContentStatus.DRAFT)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
    contentRepository.save(content);

    return contentMapper.toContentDto(content);
  }

  @Override
  @Transactional(readOnly = true)
  public ContentDto getContentById(Long id) {
    Content content = contentRepository.findById(id).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND)
    );
    User user = userRepository.findById(UserExtractor.getUserId()).get();
    if (!(content.getStatus() == ContentStatus.PUBLISHED)) {
      if (!content.getCreator().equals(user)) {
        throw new StudyHardException(ExceptionEnum.UNAUTHORIZE_CONTENT_ACCESS);
      }
    }
    return contentMapper.toContentDto(content);
  }

  @Transactional
  public CartContent createCart() {
    CartContent cartContent = CartContent.builder()
        .contentIds(new LinkedList<>(List.of(0L)))
        .userId(UserExtractor.getUserId())
        .build();
    cardContentRepository.save(cartContent);
    return cartContent;
  }

  @Override
  @Transactional
  public void addToCard(Long contentId) {
    Content content = contentRepository.findById(contentId).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND)
    );
    if (content.getCreator().getId().equals(UserExtractor.getUserId())) {
      throw new StudyHardException(ExceptionEnum.SELF_PURCHASE_NOT_ALLOWED);
    }
    CartContent cartContent = cardContentRepository.findById(UserExtractor.getUserId())
        .orElse(createCart());
    LinkedList<Long> allContentIds = cartContent.getContentIds();
    allContentIds.remove(contentId);
    allContentIds.addFirst(contentId);
    cartContent.setContentIds(allContentIds);
    cardContentRepository.save(cartContent);
  }

  @Override
  public void deleteItemsCart(Long contentId) {
    User user = userRepository.findById(UserExtractor.getUserId()).get();
    CartContent cartContent = cardContentRepository.findById(UserExtractor.getUserId())
        .orElse(createCart());
    LinkedList<Long> allContentIds = cartContent.getContentIds();
    if (!allContentIds.contains(contentId)) {
      throw new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND);
    }
    allContentIds.remove(contentId);
    cardContentRepository.save(cartContent);
  }

  @Override
  @Transactional
  public void purchaseContent(Long contentId) {
    Long userId = UserExtractor.getUserId();
    Content content = contentRepository.findById(contentId).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND)
    );
    if (content.getCreator().getId().equals(userId)) {
      throw new StudyHardException(ExceptionEnum.SELF_PURCHASE_NOT_ALLOWED);
    }
    if (content.getStatus().equals(ContentStatus.PUBLISHED)) {
      throw new StudyHardException(ExceptionEnum.CONTENT_ALREADY_PUBLISHED);
    }
    if (!content.getStatus().equals(ContentStatus.PREMIUM)) {
      throw new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND);
    }
    Optional<PurchaseContent> purchaseContentAlready = purchaseContentRepository.findByContentIdAndUserId(
        contentId, userId);
    if (purchaseContentAlready.isPresent()) {
      throw new StudyHardException(ExceptionEnum.CONTENT_IS_PURCHASED);
    }
    Wallet wallet = walletService.getOrCreateWallet(userId);
    if (wallet.getBalance().compareTo(content.getPrice()) < 0) {
      throw new StudyHardException(ExceptionEnum.INSUFFICIENT_BALANCE);
    }
    User user = userRepository.findById(UserExtractor.getUserId()).get();
    walletService.deductCredit(user, content.getPrice(), TransactionType.PURCHASE,
        "Purchase content", String.valueOf(contentId));
    Transaction transaction = transactionRepository.findTransactionByUserIdAndTypeAndReferenceId(
        userId,
        TransactionType.PURCHASE, String.valueOf(contentId));
    PurchaseContent purchaseContent = PurchaseContent.builder()
        .content(content)
        .user(user)
        .transaction(transaction)
        .createdAt(Instant.now())
        .build();
    purchaseContentRepository.save(purchaseContent);
  }

  public void hiddenBlockContent(ContentDto contentDto) {
    List<BlockDto> blocks = contentDto.getBlocks();
    for (BlockDto block : blocks) {
      for (BlockDto block2 : block.getChildren()) {
        if (!block2.getIsFree()) {
          block2.setTextContent(null);
        }
      }
    }
  }

  @Override
  public ContentDto getContentDetailById(Long contentId) {
    Content content = contentRepository.findContentDetailById(contentId);
    if (content == null) {
      throw new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND);
    }
    ContentDto contentDto = contentMapper.toContentDto(content);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.getName().equals("anonymousUser")) {
      if (!content.getCreator().getId().equals(UserExtractor.getUserId())) {
        if(content.getStatus().equals(ContentStatus.PUBLISHED)){
          return contentDto;
        }
        if(!content.getStatus().equals(ContentStatus.PREMIUM)) {
          throw new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND);
        }
        Optional<PurchaseContent> purchaseContent = purchaseContentRepository.findByContentIdAndUserId(
            contentId, UserExtractor.getUserId());
        if (purchaseContent.isPresent()) {
          hiddenBlockContent(contentDto);
        }
      }
    } else {
      hiddenBlockContent(contentDto);
    }
    contentDto.setUrlAvatarAuthor(
        fileStorageService.getImage(contentDto.getUrlAvatarAuthor(), false));
    return contentDto;
  }

  @Override
  public List<ContentDto> getAllContent() {
    List<Content> contents = contentRepository.findAllByCreator_Id(UserExtractor.getUserId());
    return contents.stream().map(contentMapper::toContentDto).toList();
  }

  @Override
  public ContentDto accessContent(Long contentId) {
    PurchaseContent purchaseContent = purchaseContentRepository.findByContentIdAndUserId(contentId,
            UserExtractor.getUserId())
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND));
    Content content = purchaseContent.getContent();
    return contentMapper.toContentDto(content);
  }

  @Override
  public List<ContentSummaryDto> getAllCard() {
    Long userId = UserExtractor.getUserId();
    Optional<CartContent> cartContent = cardContentRepository.findById(userId);
    if (cartContent.isPresent()) {
      LinkedList<Long> allContentIds = cartContent.get().getContentIds();
      List<Content> contents = new ArrayList<>();
      for (Long contentId : allContentIds) {
        if (contentId.equals(0L)) {
          continue;
        }
        contents.add(contentRepository.findById(contentId)
            .orElseThrow(() -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND)));
      }
      List<ContentSummaryDto> contentSummaries = contents.stream()
          .map(contentMapper::toContentSummaryDto).toList();
      for (ContentSummaryDto contentSummaryDto : contentSummaries) {
        if (contentSummaryDto.getUrlAvatarAuthor() != null) {
          contentSummaryDto.setUrlAvatarAuthor(
              fileStorageService.getImage(contentSummaryDto.getUrlAvatarAuthor(), false));
        }
      }
      return contentSummaries;
    }
    return List.of();
  }


  @Override
  @Transactional
  public ContentSummaryDto submitReviewContent(String contentId) {
    Content content = contentRepository.findById(Long.parseLong(contentId)).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND)
    );
    User user = userRepository.findById(UserExtractor.getUserId()).get();
    if (!content.getCreator().equals(user)) {
      throw new StudyHardException(ExceptionEnum.UNAUTHORIZE_CONTENT_ACCESS);
    }
    if (!(content.getStatus().equals(ContentStatus.DRAFT))) {
      throw new StudyHardException(ExceptionEnum.CONTENT_REVIEW_OR_REJECTION);
    }
    content.setStatus(ContentStatus.PENDING_REVIEW);
    contentRepository.save(content);
    return contentMapper.toContentSummaryDto(content);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ContentSummaryDto> getAllContentPendingReview(
      Pageable pageable) {

    Page<Content> contentPage = contentRepository.findByStatus(ContentStatus.PENDING_REVIEW,
        pageable);
    return contentPage.map(contentMapper::toContentSummaryDto);
  }

  @Override
  @Transactional
  public ContentReviewResponse reviewContent(ContentReviewRequest request) {
    Content content = contentRepository.findById(request.getContentId()).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND)
    );
    if (!content.getStatus().equals(ContentStatus.PENDING_REVIEW)) {
      throw new StudyHardException(ExceptionEnum.CONTENT_REVIEW_OR_REJECTION);
    }
    User user = userRepository.findById(UserExtractor.getUserId()).get();
    ContentReview contentReview = ContentReview.builder()
        .content(content)
        .moderator(user)
        .feedback(request.getAdminNote())
        .action(request.getReviewAction())
        .actionAt(Instant.now())
        .build();
    content.setStatus(
        request.getReviewAction().equals(ReviewAction.APPROVE)
            ? ContentStatus.PREMIUM
            : ContentStatus.REJECTED
    );
    contentRepository.save(content);
    contentReviewRepository.save(contentReview);
    return contentMapper.toContentReviewResponse(contentReview);
  }

  @Override
  public void deleteContent(Long contentId) {
    Content content = contentRepository.findById(contentId).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND)
    );
    if (!content.getCreator().getId().equals(UserExtractor.getUserId())) {
      throw new StudyHardException(ExceptionEnum.UNAUTHORIZE_CONTENT_ACCESS);
    }
    if (content.getStatus().equals(ContentStatus.PUBLISHED)) {
      throw new StudyHardException(ExceptionEnum.CONTENT_REVIEW_OR_REJECTION);
    }

    contentRepository.deleteById(contentId);

  }

  @Override
  @Transactional
  public void publishContent(String contentId) {
    Long contentIdLong = Long.parseLong(contentId);
    Content content = contentRepository.findById(contentIdLong).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND)
    );
    User user = userRepository.findById(UserExtractor.getUserId()).get();
    if (!content.getCreator().equals(user)) {
      throw new StudyHardException(ExceptionEnum.UNAUTHORIZE_CONTENT_ACCESS);
    }
    content.setStatus(ContentStatus.PUBLISHED);
    contentRepository.save(content);
  }


  @Override
  @Transactional(readOnly = true)
  public Page<ContentSummaryDto> searchContentAnyUsers(ContentSearchRequest contentSearchRequest,
      Pageable pageable) {
    Specification<Content> filter = null;
    if (contentSearchRequest != null) {
      filter = ContentPreSpecification.withFiltersUnLimited(
          contentSearchRequest);
    }
    Page<Content> contentPage = contentRepository.findAll(filter, pageable);
    Page<ContentSummaryDto> contentSummaryDtoPage = contentPage.map(
        contentMapper::toContentSummaryDto);
    List<ContentSummaryDto> contentSummaryDtoList = contentSummaryDtoPage.getContent();
    for (ContentSummaryDto contentSummaryDto : contentSummaryDtoList) {
      if (contentSummaryDto.getUrlAvatarAuthor() != null) {
        contentSummaryDto.setUrlAvatarAuthor(
            fileStorageService.getImage(contentSummaryDto.getUrlAvatarAuthor(), false));
      }
    }
    return contentSummaryDtoPage;
  }

  @Override
  public ContentDto accessContentPublish(Long contentId) {
    Content content = contentRepository.findById(contentId)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND));
    return contentMapper.toContentDto(content);
  }


}
