package com.studyhard.application.repository;

import com.studyhard.application.entity.BecomeCreator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BecomeCreatorRepository  extends CrudRepository<BecomeCreator, Long> {

}
