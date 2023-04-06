package com.pafolder.librarian.domain.service;

import com.pafolder.librarian.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

  Optional<User> getById(int id);
  User save(User user);

  void delete(int id);

  List<User> getAllFromIdToId(int fromId, int toId);

  void updateIsEnabled(int id, boolean isEnabled);

  void updateViolations(int id, int violations);



}
