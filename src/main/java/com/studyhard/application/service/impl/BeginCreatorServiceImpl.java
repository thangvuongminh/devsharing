package com.studyhard.application.service.impl;

import com.studyhard.application.dto.request.BeginCreatorRequest;
import com.studyhard.application.model.TypeFile;
import com.studyhard.application.mongo.entity.BeginCreator;
import com.studyhard.application.mongo.repository.BeginCreatorRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.service.BeginCreatorService;
import com.studyhard.application.service.FileStorageService;
import com.studyhard.application.utils.UserExtractor;
import java.time.Instant;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class BeginCreatorServiceImpl implements BeginCreatorService {
  FileStorageService fileStorageService;
  BeginCreatorRepository beginCreatorRepository;
  UserRepository userRepository;
  ChatClient chatClient;
  public BeginCreatorServiceImpl(FileStorageService fileStorageService,BeginCreatorRepository beginCreatorRepository,UserRepository userRepository,@Qualifier("evaluationUserMessages")  ChatClient chatClient) {
    this.fileStorageService = fileStorageService;
    this.beginCreatorRepository = beginCreatorRepository;
    this.userRepository = userRepository;
    this.chatClient = chatClient;
  }
  @Override
  public List<String> uploadImagesBeginCreator(List<MultipartFile> file) {
     List<String > listNameFile= fileStorageService.saveFile(file, TypeFile.BECOME_CREATOR);
     BeginCreator beginCreator = getOrCreateBeginCreator(UserExtractor.getUserId());
     List<String> allFileName=beginCreator.getThumbUrl();
     allFileName.addAll(listNameFile);
    beginCreatorRepository.save(beginCreator);
    return listNameFile;
  }

  @Override
  public BeginCreator getOrCreateBeginCreator(Long  userId) {

    return (BeginCreator)  beginCreatorRepository.findByUserId(userId).orElseGet(
        () -> {
          BeginCreator beginCreator1=BeginCreator.builder()
              .createdAt(Instant.now())
              .userId(userId)
              .build();
           beginCreatorRepository.save(beginCreator1);
          return beginCreator1;
        }
    );
  }

  @Override
  public String registerCreator(BeginCreatorRequest request) {

    return "";
  }

}
