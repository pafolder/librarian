package com.pafolder.librarian.domain.repository;

import com.pafolder.librarian.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  Optional<User> findById(int id);
  List<User> findAllFromIdToId(int fromId, int toId);

  Optional<User> findByEmail(String email);

  void updateIsEnabled(int id, boolean isEnabled);

  void updateViolations(int id, int violations);

  void deleteById(Integer id);

  User save(User user);
}
