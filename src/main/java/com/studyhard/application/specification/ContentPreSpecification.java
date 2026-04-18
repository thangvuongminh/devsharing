package com.studyhard.application.specification;

import com.studyhard.application.dto.request.ContentSearchRequest;
import com.studyhard.application.entity.Content;
import com.studyhard.application.model.ContentStatus;
import com.studyhard.application.utils.UserExtractor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
@Log4j2
public class ContentPreSpecification {

  public static Specification<Content> withFiltersByAuthor() {
    return (root, criteriaQuery, criteriaBuilder) -> {
      return criteriaBuilder.equal(root.get("creatorId"), UserExtractor.getUserId());
    };
  }
  // Filter no status
  public static Specification<Content> withFiltersUnLimited(ContentSearchRequest contentSearchRequest) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (contentSearchRequest.getKeyword()!=null){
        String keyword = "%" +  contentSearchRequest.getKeyword() + "%";
        Predicate title= criteriaBuilder.like(root.get("title"), keyword);
        Predicate description= criteriaBuilder.like(root.get("description"), keyword);
        predicates.add(criteriaBuilder.or(title,description));
      }
      if (contentSearchRequest.getCategoryName() != null) {
        Predicate   predicateCategory = criteriaBuilder.equal(root.get("category").get("name"), contentSearchRequest.getCategoryName());
        predicates.add(predicateCategory);
      }
      Predicate predicateStatus= criteriaBuilder.equal(root.get("status"), ContentStatus.PUBLISHED);
      predicates.add(predicateStatus);
      if(contentSearchRequest.getLevel()!=null){
        Predicate predicateLevel = criteriaBuilder.equal(root.get("level"), contentSearchRequest.getLevel());
        predicates.add(predicateLevel);
      }

      if(contentSearchRequest.getMinPrice()!=null){
        Predicate predicateMinPrice = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), contentSearchRequest.getMinPrice());
        predicates.add(predicateMinPrice);
      }
      if(contentSearchRequest.getMaxPrice()!=null){
        Predicate predicateMaxPrice= criteriaBuilder.lessThanOrEqualTo(root.get("price"), contentSearchRequest.getMaxPrice());
        predicates.add(predicateMaxPrice);
      }
      if(contentSearchRequest.getMinViewCount()!=null){
        Predicate  predicateMinView = criteriaBuilder.greaterThanOrEqualTo(root.get("viewCount"), contentSearchRequest.getMinViewCount());
        predicates.add(predicateMinView);
      }
      if(contentSearchRequest.getMaxViewCount()!=null){
        Predicate  predicateMinView = criteriaBuilder.lessThanOrEqualTo(root.get("viewCount"), contentSearchRequest.getMinViewCount());
        predicates.add(predicateMinView);
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}