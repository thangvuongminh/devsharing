package com.studyhard.application.controller;


import com.studyhard.application.dto.BlockDto;
import com.studyhard.application.dto.request.CreateBlockRequest;
import com.studyhard.application.dto.request.MoveBlockRequest;
import com.studyhard.application.dto.request.UpdateBlockRequest;
import com.studyhard.application.entity.Block;
import com.studyhard.application.mapper.BlockMapper;
import com.studyhard.application.mapper.ContentMapper;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("contents/{contentId}/blocks")
@Tag(name = "Content block management", description = "APIs for managing content blocks (tree structure)")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlockController {

  BlockService blockService;
  BlockMapper blockMapper;
  private final ContentMapper contentMapper;

  @PostMapping
  @Operation(summary = "Create blocks", description = "Create a new block in a content. Blocks can be nested (parent-child relationship).")
  public ResponseEntity<ApiResponse<BlockDto>> createBlock(
      @PathVariable("contentId") Long contentId,
      @RequestBody CreateBlockRequest createBlockRequest) {
    BlockDto blockDto = blockService.createBlock(contentId, createBlockRequest);
    return ResponseEntity.ok().body(ApiResponse.success(blockDto));
  }

  @PutMapping("{blockId}")
  @Operation(summary = "Updates blocks", description = "Update an existing block's content and properties"
  )
  public ResponseEntity<ApiResponse<BlockDto>> updateContentBlock(
      @PathVariable("contentId") Long contentId, @PathVariable("blockId") Long blockId,
      @RequestBody UpdateBlockRequest request) {
    Block block = blockService.updateContentBlock(contentId, blockId,
        request);
    BlockDto contentBlockDto = blockMapper.toBlockDto(block);
    return ResponseEntity.ok().body(ApiResponse.success(contentBlockDto));
  }
  @PutMapping("move/{blockId}")
  @Operation(summary = "Move Blocks")
  public ResponseEntity<ApiResponse<String>> moveBlock(
      @PathVariable("contentId") Long contentId, @PathVariable("blockId") Long blockId,
      @RequestBody MoveBlockRequest moveBlockRequest) {
     blockService.moveContentBlock(contentId, blockId,
        moveBlockRequest);
    return ResponseEntity.ok().body(ApiResponse.success("Success"));
  }
  @DeleteMapping("{blockId}")
  @Operation(summary = "Delete blocks", description = "Delete an existing block's content"
  )
  public ResponseEntity<ApiResponse<BlockDto>> deleteContentBlock(
      @PathVariable("contentId") Long contentId, @PathVariable("blockId") Long blockId) {
    blockService.deleteContentBlock(contentId, blockId);
    return ResponseEntity.ok().body(ApiResponse.success(null));
  }
  @GetMapping("{blockId}")
  @Operation(summary = "Get blocks",description = "Get an existing block's content")
  public ResponseEntity<ApiResponse<BlockDto>> getContentBlock(
      @PathVariable("contentId") Long contentId, @PathVariable("blockId") Long blockId
  ){
    Block block = blockService.getContentBlock(contentId, blockId);
    BlockDto contentBlockDto = blockMapper.toBlockDto(block);
    return ResponseEntity.ok().body(ApiResponse.success(contentBlockDto));
  }
}
