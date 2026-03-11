package com.studyhard.application.service.impl;

import com.studyhard.application.dto.ContentDto;
import com.studyhard.application.dto.ContentSummaryDto;
import com.studyhard.application.dto.request.CreateContentRequest;
import com.studyhard.application.entity.Category;
import com.studyhard.application.entity.Content;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mapper.ContentMapper;
import com.studyhard.application.model.ContentStatus;
import com.studyhard.application.redis.CartContent;
import com.studyhard.application.redis.repository.CardContentRepository;
import com.studyhard.application.repository.CategoryRepository;
import com.studyhard.application.repository.ContentRepository;
import com.studyhard.application.service.CategoryService;
import com.studyhard.application.service.ContentService;
import com.studyhard.application.utils.UserExtractor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ContentServiceImpl implements ContentService {
  ContentMapper contentMapper;
  ContentRepository contentRepository;
  CategoryRepository categoryRepository;
  CategoryService categoryService;
  CardContentRepository cardContentRepository;
  @Override
  @Transactional
  public ContentDto createContent(CreateContentRequest createContentRequest) {
    Category category = categoryService.getCategoryById(createContentRequest.getCategoryId());
    Content content = Content.builder()
        .creatorId(UserExtractor.getUserId())
        .description(createContentRequest.getDescription())
        .price(createContentRequest.getPrice())
        .title(createContentRequest.getTitle())
        .categoryId(category.getId())
        .level(createContentRequest.getContentLevel())
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
    if (!(content.getStatus() == ContentStatus.PUBLISHED)) {
      if (!content.getCreatorId().equals(UserExtractor.getUserId())) {
        throw new StudyHardException(ExceptionEnum.UNAUTHORIZE_CONTENT_ACCESS);
      }
    }
    return contentMapper.toContentDto(content);
  }
  @Transactional
  public  CartContent createCart(){
    CartContent cartContent=CartContent.builder()
        .contentIds(new LinkedHashSet<>())
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
   CartContent cartContent=cardContentRepository.findById(UserExtractor.getUserId())
       .orElse(createCart());
    LinkedHashSet<Long> allContentIds = cartContent.getContentIds();
    if(allContentIds.contains(contentId)) {
      throw new StudyHardException(ExceptionEnum.CONTENT_ALREADY_YOUR_CAR);
    }
    allContentIds.add(contentId);
    cartContent.setContentIds(allContentIds);
    cardContentRepository.save(cartContent);
  }

  @Override
  public List<ContentSummaryDto> getAllCard() {
    Long userId = UserExtractor.getUserId();
    Optional<CartContent> cartContent=cardContentRepository.findById(userId);
    if (cartContent.isPresent()) {
      LinkedHashSet<Long> allContentIds = cartContent.get().getContentIds();
      List<Content> contents = contentRepository.findBySetContentId(allContentIds);
      return contents.stream().map(contentMapper::toContentSummaryDto).toList();
    }
    return List.of();
  }

  @Override
  @Transactional
  public ContentSummaryDto submitReviewContent(String contentId) {
    Content content=contentRepository.findById(Long.parseLong(contentId)).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND)
    );
    if (!content.getCreatorId().equals(UserExtractor.getUserId())) {
      throw new StudyHardException(ExceptionEnum.UNAUTHORIZE_CONTENT_ACCESS);
    }
    if (!(content.getStatus().equals(ContentStatus.REJECTED)) && !(content.getStatus().equals(ContentStatus.PENDING_REVIEW)) ) {
      throw new StudyHardException(ExceptionEnum.CONTENT_REVIEW_OR_REJECTION);
    }
    content.setStatus(ContentStatus.REJECTED);
    contentRepository.save(content);
    return contentMapper.toContentSummaryDto(content);
  }
}
