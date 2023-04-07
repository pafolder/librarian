package com.pafolder.librarian.application.service;

import com.pafolder.librarian.domain.model.User;
import com.pafolder.librarian.domain.repository.UserRepository;
import com.pafolder.librarian.domain.service.UserService;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Named
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository repository;
  private final PasswordEncoderService passwordEncoderService;

  public List<User> getAllFromIdToId(int fromId, int toId) {
    return repository.findAllFromIdToId(fromId, toId);
  }

  public Optional<User> getById(int id) {
    return repository.findById(id);
  }

  public User getByEmail(String email) {
    return repository.findByEmail(email).orElse(null);
  }

  public User save(User user) {
    user.setPassword(passwordEncoderService.encode(user.getPassword()));
    return repository.save(user);
  }

  public void updateIsEnabled(int id, boolean isEnabled) {
    repository.updateIsEnabled(id, isEnabled);
  }

  public void updateViolations(int id, int violations) {
    repository.updateViolations(id, violations);
  }

  public void delete(int id) {
    repository.deleteById(id);
  }
}
