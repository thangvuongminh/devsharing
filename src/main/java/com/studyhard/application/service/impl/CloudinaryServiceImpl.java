package com.studyhard.application.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.studyhard.application.model.TypeFile;
import com.studyhard.application.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
@Getter
@Setter
public class CloudinaryServiceImpl implements FileStorageService {
  Cloudinary cloudinary;
  @NonFinal
  Map paramImage;
  @PostConstruct
  public  void init(){
    paramImage = new HashMap();
    paramImage.put("folder", "study-hard");
    paramImage.put("use_filename", false);
    paramImage.put("unique_filename", true);
    paramImage.put("type", "authenticated");
  }
  public  List<String> getImage(List<String> public_ids,Boolean isSecure){
    List<String> images = new ArrayList<>();
    public_ids.forEach(public_id -> {
      int index=public_id.lastIndexOf('.');
      String filename=public_id.substring(0,index);
      String format=public_id.substring(index+1);
      if(isSecure){
        try {
          images.add(cloudinary.privateDownload(filename,format,ObjectUtils.asMap(
              "type", "authenticated",
              "expire_at",(System.currentTimeMillis() / 1000) + 24 * 60 * 60
          )));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }else {
        images.add(cloudinary.url().format(format).generate(filename));
      }
    });
    return images;
  }
  @Override
  public List<String> saveFile(List<MultipartFile> files, TypeFile typeFile) {
    List<String> public_ips=new ArrayList<>();
    Map<String, Object> options = new HashMap<>(paramImage);
    Map uploadResults=null;
    try{
      if(typeFile.equals(TypeFile.CONTENT)){
        options.put("type", "upload");
      }
      for(MultipartFile file:files){
        uploadResults=cloudinary.uploader().upload(file.getBytes(),options);
        public_ips.add((String)uploadResults.get("public_id") + "." + (String)uploadResults.get("format"));
      }
    }catch(Exception e){
      e.printStackTrace();
    }
    return public_ips;
  }

  @Override
  public void deleteFile(TypeFile typeFile, List<String> fileNames) {
    try {
      List<String> publicIdsToDelete = new ArrayList<>();
      for (String name : fileNames) {
        if (name.contains(".")) {
          publicIdsToDelete.add(name.substring(0, name.lastIndexOf('.')));
        } else {
          publicIdsToDelete.add(name);
        }
      }
      Map<String, Object> options = new HashMap<>();
      if (typeFile.equals(TypeFile.CONTENT)) {
        options.put("type", "upload");
      } else {
        options.put("type", "authenticated");
      }
      Map result = cloudinary.api().deleteResources(publicIdsToDelete, options);

    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi xóa file trên Cloudinary: " + e.getMessage());
    }
  }
}
