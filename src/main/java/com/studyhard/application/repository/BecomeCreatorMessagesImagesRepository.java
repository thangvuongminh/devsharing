package com.studyhard.application.repository;

import com.studyhard.application.entity.BecomeCreatorMessagesImages;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BecomeCreatorMessagesImagesRepository extends
    CrudRepository<BecomeCreatorMessagesImages, Integer> {

}
