package com.studyhard.application.config;


import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TikaLoaderConfiguration {
  VectorStore vectorStore;
  @NonFinal
  @Value("classpath:policy/Consumer_Financial_Policies_Summary_no_bullet.pdf")
  Resource supportCustomer;
  @NonFinal
  @Value("${qdrant.init-data}")
  boolean initData;
  @PostConstruct
  public void readPolicyCompany(){
    if(initData){
      TikaDocumentReader tikaDocumentReader=new TikaDocumentReader(supportCustomer);
      List<Document> documentList=tikaDocumentReader.get();
      TokenTextSplitter  tokenTextSplitter= TokenTextSplitter.builder()
          .withChunkSize(100)
          .build();
      vectorStore.add(tokenTextSplitter.split(documentList));
    }

  }
}