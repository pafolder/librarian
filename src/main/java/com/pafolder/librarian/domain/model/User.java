package com.pafolder.librarian.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"email"},
          name = "user_unique_email_idx")
    })
public class User {

  public User(Integer id, String name, String email, String password, boolean enabled,
      int violations,
      Role role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.enabled = enabled;
    this.violations = violations;
    this.role = role;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "name", nullable = false)
  @NotBlank
  @Size(min = 2)
  private String name;

  @Column(name = "email", nullable = false, unique = true)
  @NotBlank
  @Email
  private String email;

  @Column(name = "password", nullable = false)
  @Size(max = 256)
  @NotBlank
  private String password;

  @Column(name = "enabled", nullable = false, columnDefinition = "boolean default true")
  private boolean enabled = true;

  @Column(name = "violations", columnDefinition = "integer default 0")
  private int violations;

  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private Collection<Checkout> checkouts = new ArrayList<>();

  public List<Checkout> activeCheckouts() {
    return checkouts.stream()
        .filter(c -> Optional.ofNullable(c.getCheckinDateTime()).isEmpty())
        .sorted(Comparator.comparing(Checkout::getCheckoutDateTime))
        .collect(Collectors.toList());
  }

  public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
      return "ROLE_" + name();
    }
  }
}
