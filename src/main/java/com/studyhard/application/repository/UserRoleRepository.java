package com.studyhard.application.repository;

import com.studyhard.application.entity.Role;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole,Long> {
  List<UserRole> findByUser(User user);
  List<UserRole> findByUserId(Long userId);
  @Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.user.id = :userId")
  List<UserRole> findByUserCustomQuery(Long userId);

  UserRole findByRole(Role role);

  UserRole findByUserAndRole(User user, Role role);
}
