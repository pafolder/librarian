package com.pafolder.librarian.infrastructure.security;

import com.pafolder.librarian.domain.model.User;
import com.pafolder.librarian.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public User getByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
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
