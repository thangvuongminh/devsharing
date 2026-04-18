package com.studyhard.application.mapper;


import com.studyhard.application.dto.ContentDto;
import com.studyhard.application.dto.ContentSummaryDto;
import com.studyhard.application.dto.response.ContentReviewResponse;
import com.studyhard.application.entity.Content;
import com.studyhard.application.entity.ContentReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ContentMapper {
  @Value("${app.contents.url}")
  public String contentsUrl;
  public   abstract   ContentDto toContentDto(Content content);
  @Mapping(source = "category.name", target = "categoryName")
  @Mapping(target = "thumb",expression = "java(contentsUrl + \"/\" + content.getThumb())")
  public  abstract ContentSummaryDto toContentSummaryDto(Content content);
  public  abstract ContentReviewResponse toContentReviewResponse(ContentReview contentReview);
}