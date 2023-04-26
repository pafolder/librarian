package com.pafolder.librarian.infrastructure.repository;

import com.pafolder.librarian.domain.model.User;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>,
    com.pafolder.librarian.domain.repository.UserRepository {
  @Query("SELECT u FROM User u WHERE u.id BETWEEN :fromId AND :toId ORDER BY u.id")
  @EntityGraph(attributePaths = "role")
  List<User> findAllFromIdToId(int fromId, int toId);

  @Cacheable("users")
  Optional<User> findByEmail(String email);

  @CacheEvict(value = "users", allEntries = true)
  @Query("UPDATE User u SET u.enabled = :isEnabled WHERE u.id = :id")
  @Modifying
  void updateIsEnabled(@Parameter int id, @Parameter boolean isEnabled);

  @CacheEvict(value = "users", allEntries = true)
  @Query("UPDATE User u SET u.violations = :violations WHERE u.id = :id")
  @Modifying
  void updateViolations(@Parameter int id, @Parameter int violations);

  @CacheEvict(value = "users", allEntries = true)
  void deleteById(Integer id);

  @CacheEvict(value = "users", allEntries = true)
  User save(User user);
}
