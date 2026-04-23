package com.studyhard.application.repository;

import com.studyhard.application.entity.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile,Integer> {

  Optional<UserProfile> findByUserId(Long userId);

  @Query("select u from UserProfile u join fetch u.user where u.nickName = :nickName")
  UserProfile findUserByNickName(String nickName);
}
