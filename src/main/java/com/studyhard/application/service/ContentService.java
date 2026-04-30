package com.studyhard.application.service;

import com.studyhard.application.dto.ContentDto;
import com.studyhard.application.dto.ContentSummaryDto;
import com.studyhard.application.dto.request.ContentReviewRequest;
import com.studyhard.application.dto.request.ContentSearchRequest;
import com.studyhard.application.dto.request.CreateContentRequest;
import com.studyhard.application.dto.response.ContentReviewResponse;
import com.studyhard.application.entity.Content;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentService {

  //  CRUD CONTENT
  public ContentDto createContent(CreateContentRequest createContentRequest);

  public ContentDto getContentById(Long id);

  public void addToCard(Long contentId);

  public List<ContentSummaryDto> getAllCard( );

  public ContentSummaryDto submitReviewContent(String contentId);

  public Page<ContentSummaryDto> getAllContentPendingReview(Pageable pageable);

  public ContentReviewResponse reviewContent(ContentReviewRequest request);

  public  void deleteContent(Long contentId);

  public void publishContent(String contentId);

  public  void deleteItemsCart(Long contentId);

  public void purchaseContent(Long contentId);

  public ContentDto getContentDetailById(Long contentId);

  public  List<ContentDto> getAllContent();

  public  ContentDto accessContent(Long contentId);
//  public Content getContentById(Long id);
//
//  public Content updateContent(Long id, UpdateContentRequest updateContentRequest);
//
//  public void deleteContentById(Long id);
//
//  // search and filter pagination
//  public Page<Content> searchContentByAuthor(ContentSearchRequest contentSearchRequest);
  public Page<ContentSummaryDto> searchContentAnyUsers(ContentSearchRequest contentSearchRequest,Pageable pageable);

  public  ContentDto accessContentPublish(Long contentId);
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