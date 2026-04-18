package com.studyhard.application.mongo.repository;

import com.studyhard.application.mongo.entity.BeginCreator;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeginCreatorRepository extends MongoRepository<BeginCreator,String > {

  Optional<Object> findByUserId(Long userId);
}
