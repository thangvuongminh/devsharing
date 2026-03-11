package com.studyhard.application.repository;

import com.studyhard.application.entity.ContentReview;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentReviewRepository extends CrudRepository<ContentReview, Long> {

}
