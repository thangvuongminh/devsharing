package com.studyhard.application.service;

import com.studyhard.application.dto.BlockDto;
import com.studyhard.application.dto.request.CreateBlockRequest;
import com.studyhard.application.dto.request.MoveBlockRequest;
import com.studyhard.application.dto.request.UpdateBlockRequest;
import com.studyhard.application.entity.Block;

public interface BlockService {
  public BlockDto createBlock(Long contentId, CreateBlockRequest createBlockRequest);
  public Block updateContentBlock(Long contentId,Long blockId, UpdateBlockRequest updateContentBlockRequest);
  public void deleteContentBlock(Long contentId,Long blockId);
  public Block getContentBlock(Long contentId,Long blockId);
  public void moveContentBlock(Long contentId,Long blockId, MoveBlockRequest moveBlockRequest);
}
