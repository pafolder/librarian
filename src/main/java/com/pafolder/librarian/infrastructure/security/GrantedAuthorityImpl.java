package com.pafolder.librarian.infrastructure.security;

import com.pafolder.librarian.domain.model.User.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public class GrantedAuthorityImpl implements GrantedAuthority {

  private final Role role;

  @Override
  public String getAuthority() {
    return "ROLE_" + role.name();
  }
}
