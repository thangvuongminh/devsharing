package com.studyhard.application.service.impl;

import com.studyhard.application.dto.request.CreateBlockRequest;
import com.studyhard.application.dto.request.MoveBlockRequest;
import com.studyhard.application.dto.request.UpdateBlockRequest;
import com.studyhard.application.entity.Block;
import com.studyhard.application.entity.Content;
import com.studyhard.application.entity.User;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.repository.BlockRepository;
import com.studyhard.application.repository.ContentRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.service.BlockService;
import com.studyhard.application.utils.UserExtractor;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional
public class BlockServiceImpl implements BlockService {
  ContentRepository contentRepository;
  BlockRepository blockRepository;
  UserRepository userRepository;
  @Override
  @Transactional
  public Block createBlock(Long contentId, CreateBlockRequest createBlockRequest) {
    Content content = contentRepository.findById(contentId)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND));
    User user= userRepository.findById(UserExtractor.getUserId()).get();
    if (!content.getCreator().equals(user)) {
      throw new StudyHardException(ExceptionEnum.UNAUTHORIZE_CONTENT_ACCESS);
    }
    Block parentContentBlock = null;
    if (createBlockRequest.getParentBlockId() != null) {
      parentContentBlock = blockRepository.findById(createBlockRequest.getParentBlockId())
          .orElseThrow(() -> new StudyHardException(ExceptionEnum.BLOCK_NOT_FOUND));
      if (!parentContentBlock.getContent().getId().equals(contentId)) {
        throw new StudyHardException(ExceptionEnum.PARENT_BLOCK_NOT_IN_SAME_CONTENT);
      }
    }
    adjustPositionBlock(contentId, createBlockRequest.getParentBlockId(),
        createBlockRequest.getPosition(), false);
    Block contentBlock = Block.builder().content(content)
        .parentBlock(parentContentBlock).textContent(createBlockRequest.getTextContent())
        .position(createBlockRequest.getPosition()).isFree(createBlockRequest.getIsFree())
        .properties(createBlockRequest.getProperties()).type(createBlockRequest.getType())
        .createdAt(Instant.now()).updatedAt(Instant.now()).build();
    blockRepository.save(contentBlock);
    return contentBlock;
  }

  @Override
  @Transactional
  public Block updateContentBlock(Long contentId, Long blockId,
      UpdateBlockRequest updateContentBlockRequest) {
    Block contentBlock = checkAuthorizeContent(contentId, blockId);
    if (updateContentBlockRequest.getIsFree() != null) {
      contentBlock.setIsFree(updateContentBlockRequest.getIsFree());
    }
    if (updateContentBlockRequest.getTextContent() != null) {
      contentBlock.setTextContent(updateContentBlockRequest.getTextContent());
    }
    if (updateContentBlockRequest.getProperties() != null) {
      contentBlock.setProperties(updateContentBlockRequest.getProperties());
    }
    if (updateContentBlockRequest.getType() != null) {
      contentBlock.setType(updateContentBlockRequest.getType());
    }
    blockRepository.save(contentBlock);
    return contentBlock;
  }

  @Override
  @Transactional
  public void deleteContentBlock(Long contentId, Long blockId) {
    Block contentBlock = checkAuthorizeContent(contentId, blockId);
    Long parentBlockId=null;
    if (contentBlock.getParentBlock() != null) {
      parentBlockId = contentBlock.getParentBlock().getId();
    }
    adjustPositionBlock(contentId, parentBlockId,contentBlock.getPosition(),true);

    blockRepository.deleteById(blockId);
  }

  @Override
  public Block getContentBlock(Long contentId, Long blockId) {
    return checkAuthorizeContent(contentId, blockId);
  }

  @Override
  public Block moveContentBlock(Long contentId, Long blockId,
      MoveBlockRequest moveBlockRequest) {
    return null;
  }

  public Block checkAuthorizeContent(Long contentId, Long blockId) {
    Content content = contentRepository.findById(contentId)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.CONTENT_NOT_FOUND));
    User user= userRepository.findById(UserExtractor.getUserId()).get();
    if (!content.getCreator().equals(user)) {
      throw new StudyHardException(ExceptionEnum.UNAUTHORIZE_CONTENT_ACCESS);
    }
    return blockRepository.findById(blockId)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.BLOCK_NOT_FOUND));
  }

  public void adjustPositionBlock(Long contentId, Long parentBlockId, Integer positionBlock,
      Boolean isDeleted) {
    List<Block> contentBlockList;
    if (parentBlockId == null) {
      contentBlockList = blockRepository.findByContentIdAndPositionIsNotNullOrderByPositionAsc(
          contentId);
    } else {
      contentBlockList = blockRepository.findByParentBlockIdAndPositionIsNotNullOrderByPosition(
          parentBlockId);
    }
    contentBlockList.stream().filter(contentBlock -> contentBlock.getPosition() >= positionBlock)
        .forEach(contentBlock -> {
          contentBlock.setPosition(
              isDeleted ? contentBlock.getPosition() - 1 : contentBlock.getPosition() + 1);
        });
    blockRepository.saveAll(contentBlockList);
  }

}
