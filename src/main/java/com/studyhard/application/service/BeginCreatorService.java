package com.studyhard.application.service;

import com.studyhard.application.dto.request.BeginCreatorRequest;
import com.studyhard.application.mongo.entity.BeginCreator;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface BeginCreatorService {

  public List<String> uploadImagesBeginCreator(List<MultipartFile> file);
  public BeginCreator getOrCreateBeginCreator(Long userId);
  public String registerCreator(BeginCreatorRequest request);
}
