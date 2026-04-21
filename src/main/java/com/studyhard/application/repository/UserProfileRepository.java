package com.studyhard.application.repository;

import com.studyhard.application.entity.UserProfile;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile,Integer> {

  Optional<UserProfile> findByUserId(Long userId);
}
