package com.studyhard.application.service;

import com.studyhard.application.dto.ContentDto;
import com.studyhard.application.dto.ContentSummaryDto;
import com.studyhard.application.dto.request.CreateContentRequest;
import com.studyhard.application.entity.Content;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ContentService {

  //  CRUD CONTENT
  public ContentDto createContent(CreateContentRequest createContentRequest);

  public ContentDto getContentById(Long id);

  public void addToCard(Long contentId);

  public List<ContentSummaryDto> getAllCard();

  public ContentSummaryDto submitReviewContent(String contentId);
//  public Content getContentById(Long id);
//
//  public Content updateContent(Long id, UpdateContentRequest updateContentRequest);
//
//  public void deleteContentById(Long id);
//
//  // search and filter pagination
//  public Page<Content> searchContentByAuthor(ContentSearchRequest contentSearchRequest);
//  public Page<Content> searchContentAnyUsers(ContentSearchRequest contentSearchRequest);
//
//  // content management
//  public Content publishContent(Long contentId) ;
//  public Content archiveContent(Long contentId);
//  public Content submitForReview(Long contentId);
//
//  // Tracking (called by other services)
//  public void incrementViewCount(Long contentId);
//  public void incrementPurchaseCount(Long contentId);


}