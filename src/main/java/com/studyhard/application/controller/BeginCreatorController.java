package com.studyhard.application.controller;

import com.studyhard.application.dto.request.BeginCreatorRequest;
import com.studyhard.application.mongo.entity.BeginCreator;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.BeginCreatorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("creator")
public class BeginCreatorController {
  BeginCreatorService beginCreatorService;
  @PostMapping(value = "register",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(description = "CONSUMER register moderator")
  @PreAuthorize("hasRole('CONSUMER')")
  public ResponseEntity<ApiResponse<String>> registerCreator(@ModelAttribute BeginCreatorRequest request) {
    beginCreatorService.registerCreator(request);
    return  null;
  }

}
