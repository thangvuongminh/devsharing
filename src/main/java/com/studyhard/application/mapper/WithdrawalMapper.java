package com.studyhard.application.mapper;

import com.studyhard.application.dto.WithdrawalRequestDto;
import com.studyhard.application.entity.WithdrawalRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedSourcePolicy = ReportingPolicy.IGNORE,unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WithdrawalMapper {

  WithdrawalRequestDto toWithdrawalRequestDto(WithdrawalRequest withdrawalRequest);
}