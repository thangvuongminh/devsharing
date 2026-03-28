package com.studyhard.application.service;

import com.studyhard.application.model.TypeFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
  public void init(Path pathStore) ;
  public List<String>  saveFile(List<MultipartFile> files, TypeFile typeFile);
  public void deleteFile(TypeFile typeFile,List<String> fileNames);
}
