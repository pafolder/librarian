package com.pafolder.librarian.service;

import com.pafolder.librarian.model.User;
import com.pafolder.librarian.repository.UserRepository;
import com.pafolder.librarian.security.UserDetailsImpl;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserDetailsService {
  private final UserRepository repository;
  private PasswordEncoder passwordEncoder;

  public List<User> getAllFromIdToId(int fromId, int toId) {
    return repository.findAllFromIdToId(fromId, toId);
  }

  public Optional<User> getById(int id) {
    return repository.findById(id);
  }

  public User getByEmail(String email) {
    return repository.findByEmail(email).orElse(null);
  }

  @CacheEvict(value = "users", allEntries = true)
  public User save(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return repository.save(user);
  }

  @CacheEvict(value = "users", allEntries = true)
  public void updateIsEnabled(int id, boolean isEnabled) {
    repository.updateIsEnabled(id, isEnabled);
  }

  @CacheEvict(value = "users", allEntries = true)
  public void updateViolations(int id, int violations) {
    repository.updateViolations(id, violations);
  }

  @CacheEvict(value = "users", allEntries = true)
  public void delete(int id) {
    repository.deleteById(id);
  }

  public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = getByEmail(email.toLowerCase());
    if (user == null) {
      throw new UsernameNotFoundException("No such user " + email.toLowerCase());
    }
    return new UserDetailsImpl(user);
  }
}
