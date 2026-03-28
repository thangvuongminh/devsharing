package com.studyhard.application.service.impl;

import com.studyhard.application.model.TypeFile;
import com.studyhard.application.service.FileStorageService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Io;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileStorageServiceImpl implements FileStorageService {
  Path root= Paths.get("D:\\dev-sharing");
  @Override
  public void init(Path pathStore) {
    try {
      if(!Files.exists(pathStore)) {
        Files.createDirectories(pathStore);
      }
    }catch (IOException e){
//      e.printStackTrace();
    }
  }


  public List<String>  saveFile(List<MultipartFile> files, TypeFile typeFile){
    try {
          Path pathStore=root.resolve(typeFile.getUrl());
          init(pathStore);
          List<String>  fileNames=new ArrayList<>();
          for (MultipartFile multipartFile:files){
            String fileName= UUID.randomUUID().toString() + multipartFile.getOriginalFilename() ;
              Files.copy(multipartFile.getInputStream(), pathStore.resolve(fileName));
              String pathFilename=typeFile.getUrl()+"\\"+fileName;
              fileNames.add(fileName);
          }
          return fileNames;
    }catch (Exception e){
      e.printStackTrace();
    }
    return List.of();
  }
  @Override
  public void deleteFile(TypeFile typeFile,List<String> fileNames){
    try {
      Path pathStore=root.resolve(typeFile.getUrl());
      Path pathDelete=null;
      for (String name:fileNames){
        pathDelete=pathStore.resolve(name);
        Files.delete(pathDelete);
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
