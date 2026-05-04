package com.studyhard.application.mapper;


import com.studyhard.application.dto.BlockDto;
import com.studyhard.application.entity.Block;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedSourcePolicy = ReportingPolicy.IGNORE,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlockMapper {
  @Mapping(target = "contentId" ,source = "content.id")
  BlockDto toBlockDto(Block block);
}