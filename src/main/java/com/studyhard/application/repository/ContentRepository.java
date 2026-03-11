package com.studyhard.application.repository;

import com.studyhard.application.entity.Content;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content,Long> {
  @Query("select c from  Content c where c.id in( :setContent) ")
  List<Content> findBySetContentId(@Param("setContent") LinkedHashSet<Long> setContent);
}
