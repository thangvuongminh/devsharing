package com.studyhard.application.repository;
import com.studyhard.application.entity.Block;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block,Long> {
  List<Block> findByContentIdAndPositionIsNotNullOrderByPositionAsc(Long contentId);
  List<Block> findByParentBlockIdAndPositionIsNotNullOrderByPosition(Long contentId);

}