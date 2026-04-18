package com.studyhard.application.controller;

import com.studyhard.application.dto.ContentDto;
import com.studyhard.application.dto.ContentSummaryDto;
import com.studyhard.application.dto.request.ContentReviewRequest;
import com.studyhard.application.dto.request.ContentSearchRequest;
import com.studyhard.application.dto.request.CreateContentRequest;
import com.studyhard.application.dto.response.ContentReviewResponse;
import com.studyhard.application.entity.Content;
import com.studyhard.application.mapper.ContentMapper;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.ContentService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.print.attribute.standard.Media;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Content management", description = "Apis for content")
public class ContentController {

  ContentService contentService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Create content")
  @PreAuthorize("hasRole('CREATOR')")
  public ResponseEntity<ApiResponse<ContentDto>> createContent(
      @Valid @ModelAttribute CreateContentRequest createContentRequest) {
    ContentDto contentDto = contentService.createContent(createContentRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(contentDto));
  }

  @GetMapping("/{contentId:\\d+}")
  @PreAuthorize("hasRole('CREATOR')")
  @Operation(summary = "Get content by id only user", description = "Get detailed content information")
  public ResponseEntity<ApiResponse<ContentDto>> getContentById(@PathVariable long contentId) {
    ContentDto contentDto = contentService.getContentById(contentId);
    return ResponseEntity.ok().body(ApiResponse.success(contentDto));
  }

  @GetMapping("/all/contests")
  @PreAuthorize("hasRole('CREATOR')")
  @Operation(summary = "Get all contents by id only user", description = "Get all contents")
  public ResponseEntity<ApiResponse<List<ContentDto>>> getAllContents(@Parameter(hidden = true)
  @PageableDefault(
      page = 0,
      size = 20,
      sort = "createdAt",
      direction = Direction.DESC
  ) Pageable pageable) {
    List<ContentDto> contentDto = contentService.getAllContent(pageable);
    return ResponseEntity.ok().body(ApiResponse.success(contentDto));
  }

  @PostMapping("/add/cart")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  @Operation(summary = "Add content  to cart")
  public ResponseEntity<ApiResponse<Void>> addToCard(@RequestBody Map<String, String> body) {
    String contentId = body.get("contentId");
    Long contentLong= Long.parseLong(contentId);
    contentService.addToCard(contentLong);
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

  @GetMapping("get/pending")
  @Operation(summary = "Get all content pending review", parameters = {
      @Parameter(name = "page", description = "Page number", example = "0"),
      @Parameter(name = "size", description = "Page size", example = "10"),
      @Parameter(name = "sort", description = "Sort field,direction", example = "createdAt,desc", required = false)
  })
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<ApiResponse<Page<ContentSummaryDto>>> getAllContentPendingReview(
      @Parameter(hidden = true)
      @PageableDefault(
          page = 0,
          size = 20,
          sort = "createdAt",
          direction = Direction.DESC
      ) Pageable pageable) {
    Page<ContentSummaryDto> response = contentService.getAllContentPendingReview(pageable);
    return ResponseEntity.ok().body(ApiResponse.success(response
    ));
  }

  @PostMapping("/review")
  @Operation(summary = "Review Content")
  @PreAuthorize("hasAnyRole('MODERATOR')")
  public ResponseEntity<ApiResponse<ContentReviewResponse>> reviewContent(@RequestBody
      ContentReviewRequest request) {
    ContentReviewResponse contentReviewResponse = contentService.reviewContent(request);
    return ResponseEntity.ok().body(ApiResponse.success(contentReviewResponse
    ));
  }

  @GetMapping("/{contentId}/publish")
  @Operation(summary = "Creator public content")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<Void>> publishContent(@PathVariable String contentId) {
    contentService.publishContent(contentId);
    return ResponseEntity.ok().body(ApiResponse.success(null));
  }

  @GetMapping("/{contentId}/access")
  @Operation(summary = "AccessContent  when purchased")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<ContentDto>> accessContent(@PathVariable Long contentId) {
    ContentDto contentDto=  contentService.accessContent(contentId);
    return ResponseEntity.ok().body(ApiResponse.success(contentDto));
  }

  @PostMapping("/search")
  @Operation(summary = "SearchContent", parameters = {
      @Parameter(name = "page", description = "Page number", example = "0"),
      @Parameter(name = "size", description = "Page size", example = "10"),
      @Parameter(name = "sort", description = "Sort field,direction", example = "createdAt,desc", required = false)
  })
  public ResponseEntity<ApiResponse<Page<ContentSummaryDto>>> searchContentAnyUser(
      @RequestBody(required = false) ContentSearchRequest contentSearchRequest,@Parameter(hidden = true) @PageableDefault(
          size = 20,
          page = 0,
          sort = "createdAt",
          direction = Direction.DESC
      ) Pageable pageable ) {
    Page<ContentSummaryDto> contentSummaryDtos=  contentService.searchContentAnyUsers(contentSearchRequest,pageable);
    return ResponseEntity.ok().body(ApiResponse.success(contentSummaryDtos));
  }

  @DeleteMapping("/{contentId}/delete/cart")
  @Operation(summary = "DELETE CONTENT in the cart")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  public ResponseEntity<ApiResponse<String>> deleteContentCart(@PathVariable Long contentId) {
    contentService.deleteItemsCart(contentId);
    return ResponseEntity.ok().body(ApiResponse.success("Delete content in cart success"));
  }

  @DeleteMapping("/{contentId}/delete/creator")
  @Operation(summary = "DELETE CONTENT BY CREATOR")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<String>> deleteContentCreator(@PathVariable Long contentId) {
    contentService.deleteContent(contentId);
    return ResponseEntity.ok().body(ApiResponse.success("Delete content  success"));
  }

  @GetMapping("/{contentId}/purchase")
  @Operation(summary = "purchase in the cart")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  public ResponseEntity<ApiResponse<String>> purchaseContent(@PathVariable Long contentId) {
    contentService.purchaseContent(contentId);
    return ResponseEntity.ok().body(ApiResponse.success("Delete content in cart success"));
  }

  @GetMapping("/{contentId}/access/public")
  @Operation(summary = "Access content no purchase")
  public ResponseEntity<ApiResponse<ContentDto>> accessContentNoPurchase(@PathVariable Long contentId) {
    ContentDto contentDto=  contentService.accessContent(contentId);
    return ResponseEntity.ok().body(ApiResponse.success(contentDto));
  }
}
