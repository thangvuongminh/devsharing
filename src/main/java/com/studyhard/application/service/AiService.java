package com.studyhard.application.service;

import com.studyhard.application.dto.request.RegisterCreatorRequest;
import com.studyhard.application.dto.response.RegisterCreatorResponse;
import java.io.IOException;

public interface AiService {
  public RegisterCreatorResponse verifyCreator(RegisterCreatorRequest registerCreatorRequest) throws IOException;
}
