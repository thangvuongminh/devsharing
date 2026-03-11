package com.studyhard.application.redis.repository;

import com.studyhard.application.redis.CartContent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardContentRepository extends CrudRepository<CartContent,Long> {

}
