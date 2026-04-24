package com.studyhard.application.controller;

import com.studyhard.application.dto.request.RegisterCreatorRequest;
import com.studyhard.application.dto.response.RegisterCreatorResponse;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/ai")
public class AiController {
  AiService aiService;
  @PostMapping( value = "/register/creator" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "user register  creator")
  @PreAuthorize("hasRole('CONSUMER')")
  public ResponseEntity<ApiResponse<?>> registerCreator(@ModelAttribute RegisterCreatorRequest request)
      throws IOException {
      RegisterCreatorResponse response= aiService.verifyCreator(request);
      if("NO".equalsIgnoreCase(response.getResults())){
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(response.getReason(),"Verify not successful"));
      }
    return  ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
