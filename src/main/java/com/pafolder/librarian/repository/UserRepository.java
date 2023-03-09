package com.pafolder.librarian.repository;

import com.pafolder.librarian.model.User;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Integer> {
  @Query("SELECT u FROM User u WHERE u.id BETWEEN :fromId AND :toId ORDER BY u.id")
  @EntityGraph(attributePaths = "role")
  List<User> findAllFromIdToId(int fromId, int toId);

  @Cacheable("users")
  Optional<User> findByEmail(String email);

  @Transactional
  @Query("UPDATE User u SET u.enabled = :isEnabled WHERE u.id = :id")
  @Modifying
  void updateIsEnabled(@Parameter int id, @Parameter boolean isEnabled);

  @Transactional
  @Query("UPDATE User u SET u.violations = :violations WHERE u.id = :id")
  @Modifying
  void updateViolations(@Parameter int id, @Parameter int violations);
}
