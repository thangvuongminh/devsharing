package com.studyhard.application.repository;

import com.studyhard.application.entity.User;
import com.studyhard.application.entity.Wallet;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select w from Wallet w where w.user= :user ")
  Optional<Wallet> findByUserIdWithLock(@Param("user") User user);
  Optional<Wallet> findByUserId(Long userId);
}
