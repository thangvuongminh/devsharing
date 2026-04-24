package com.studyhard.application.repository;

import com.studyhard.application.entity.Content;
import com.studyhard.application.model.ContentStatus;
import com.studyhard.application.model.ReviewAction;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content,Long>,
    JpaSpecificationExecutor<Content> {
  @Query("select c from  Content c where c.id in( :setContent) ")
  List<Content> findBySetContentId(@Param("setContent") LinkedHashSet<Long> setContent);
  @EntityGraph(attributePaths = {"creator"})
  Page<Content> findAll(Specification<Content> specification,Pageable pageable);
  Page<Content> findByStatus(ContentStatus status, Pageable pageable);
  @EntityGraph(attributePaths = {"creator"})
  List<Content> findAllByCreator_Id(Long creatorId);
}
