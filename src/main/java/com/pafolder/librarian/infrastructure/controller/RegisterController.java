package com.pafolder.librarian.infrastructure.controller;

import com.pafolder.librarian.domain.model.User;
import com.pafolder.librarian.domain.service.UserService;
import com.pafolder.librarian.infrastructure.controller.validator.UserToValidator;
import com.pafolder.librarian.infrastructure.controller.view.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@AllArgsConstructor
@Tag(name = "4 register-controller")
@RequestMapping(value = RegisterController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegisterController {
  public static final String REST_URL = "/api/register";
  private final Logger log = LoggerFactory.getLogger(getClass());
  private UserService userService;
  protected UserToValidator userToValidator;

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(userToValidator);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Register a new User",
      security = {@SecurityRequirement(name = "basicScheme")})
  public ResponseEntity<UserView> register(@Valid @RequestBody UserTo userTo) {
    log.info("register()");
    User created =
        userService.save(
            new User(
                null,
                userTo.getName(),
                userTo.getEmail(),
                userTo.getPassword(),
                false,
                0,
                User.Role.USER));
    URI uriOfNewResource =
        ServletUriComponentsBuilder.fromCurrentContextPath().path(REST_URL).build().toUri();
    return ResponseEntity.created(uriOfNewResource).body(new UserView(created));
  }
}
