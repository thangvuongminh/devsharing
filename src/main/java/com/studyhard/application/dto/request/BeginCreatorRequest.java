package com.studyhard.application.dto.request;


import com.studyhard.application.mongo.entity.ChatMessage;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BeginCreatorRequest {
  List<BeginCreatorInner> beginCreatorInners;
  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class BeginCreatorInner{
    ChatMessageRequest chatMessageRequest;
    MultipartFile image;
  }
}
