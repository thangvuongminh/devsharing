package com.studyhard.application.controller;

import com.studyhard.application.dto.ContentDto;
import com.studyhard.application.dto.ContentSummaryDto;
import com.studyhard.application.dto.request.ContentReviewRequest;
import com.studyhard.application.dto.request.CreateContentRequest;
import com.studyhard.application.dto.response.ContentReviewResponse;
import com.studyhard.application.entity.Content;
import com.studyhard.application.mapper.ContentMapper;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Stack;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Content management", description = "Apis for content")
public class ContentController {

  ContentService contentService;

  @PostMapping
  @Operation(summary = "Create content")
  @PreAuthorize("hasRole('CREATOR')")
  public ResponseEntity<ApiResponse<ContentDto>> createContent(
      @RequestBody CreateContentRequest createContentRequest) {
    ContentDto contentDto = contentService.createContent(createContentRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(contentDto));
  }

  @GetMapping("/{contentId}")
  @PreAuthorize("hasRole('CREATOR')")
  @Operation(summary = "Get content by id", description = "Get detailed content information")
  public ResponseEntity<ApiResponse<ContentDto>> getContentById(@PathVariable long contentId) {
    ContentDto contentDto = contentService.getContentById(contentId);
    return ResponseEntity.ok().body(ApiResponse.success(contentDto));
  }

  @GetMapping("/add/cart/{contentId}")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  @Operation(summary = "Add content  to cart")
  public ResponseEntity<ApiResponse<Void>> addToCard(@PathVariable long contentId) {
    contentService.addToCard(contentId);
    return ResponseEntity.ok().body(ApiResponse.success(null));
  }

  @GetMapping("/cart")
  @Operation(summary = "Get content no purchase")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  public ResponseEntity<ApiResponse<List<ContentSummaryDto>>> getAllCard() {
    List<ContentSummaryDto> contentSummaryDtos = contentService.getAllCard();
    return ResponseEntity.ok().body(ApiResponse.success(contentSummaryDtos));
  }

  @GetMapping("/{contentId}/submit-review")
  @Operation(summary = "Send moderator  preview")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<ContentSummaryDto>> submitReviewContent(
      @PathVariable("contentId") String contentId) {
    ContentSummaryDto contentSummaryDto = contentService.submitReviewContent(contentId);
    return ResponseEntity.ok().body(ApiResponse.success(contentSummaryDto));
  }

  @GetMapping("get/content/pending")
  @Operation(summary = "Get all conent pending preview")
  @PreAuthorize("hasAnyRole('MODERATOR')")
  public ResponseEntity<ApiResponse<Page<ContentSummaryDto>>> getAllContentPendingReview(Pageable pageable) {
    Page<ContentSummaryDto> response= contentService.getAllContentPendingReview(pageable);
    return ResponseEntity.ok().body(ApiResponse.success(response
    ));
  }
  @GetMapping("/review")
  @Operation(summary = "Review Content")
  @PreAuthorize("hasAnyRole('MODERATER')")
  public ResponseEntity<ApiResponse<ContentReviewResponse>> reviewContent(ContentReviewRequest request) {
   ContentReviewResponse contentReviewResponse= contentService.reviewContent(request);
    return ResponseEntity.ok().body(ApiResponse.success(contentReviewResponse
    ));
  }
  @GetMapping("/{contentId}/publish")
  @Operation(summary = "Creator public content")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<Void>> publishContent(@PathVariable  String contentId  ) {
    contentService.publishContent(contentId);
    return ResponseEntity.ok().body(ApiResponse.success(null));
  }

}
