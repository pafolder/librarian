package com.pafolder.librarian.infrastructure.controller.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pafolder.librarian.domain.model.User;
import com.pafolder.librarian.domain.model.User.Role;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class UserView {

  private User user;

  public Integer getId() {
    return user.getId();
  }

  public String getName() {
    return user.getName();
  }

  public String getEmail() {
    return user.getEmail();
  }

  public int getViolations() {
    return user.getViolations();
  }

  public Role getRole() {
    return user.getRole();
  }
}
