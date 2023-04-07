package com.pafolder.librarian.controller.profile;

import static com.pafolder.librarian.util.ControllerUtil.getFutureViolations;

import com.pafolder.librarian.controller.admin.AdminUserController;
import com.pafolder.librarian.model.User;
import com.pafolder.librarian.repository.CheckoutRepository;
import com.pafolder.librarian.security.UserDetailsImpl;
import com.pafolder.librarian.service.UserServiceImpl;
import com.pafolder.librarian.to.UserTo;
import com.pafolder.librarian.validator.UserToValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "3 profile-controller")
@RequestMapping(value = ProfileController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileController {
  public static final String REST_URL = "/api/profile";
  private final Logger log = LoggerFactory.getLogger(getClass());
  private UserServiceImpl userService;
  protected UserToValidator userToValidator;
  private final CheckoutRepository checkoutRepository;

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(userToValidator);
  }

  @GetMapping
  @Operation(
      summary = "Get authenticated User's credentials",
      security = {@SecurityRequirement(name = "basicScheme")})
  public User getAuth(@AuthenticationPrincipal UserDetailsImpl authUser) {
    log.info("getAuth()");
    User userWithFutureViolations = userService.getById(authUser.getUser().getId()).orElseThrow();
    userWithFutureViolations.setViolations(
        getFutureViolations(userWithFutureViolations, checkoutRepository));
    return userWithFutureViolations;
  }

  @PutMapping
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Update authenticated User's credentials",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "userTo", description = "Updated user's credentials")
  /*    @Transactional*/
  public void updateAuth(
      @Valid @RequestBody UserTo userTo,
      @AuthenticationPrincipal UserDetailsImpl authUser,
      HttpServletRequest request)
      throws ServletException {
    log.info("updateAuth()");
    int id = authUser.getUser().getId();
    AdminUserController.protectAdminPreset(id);
    User updated =
        new User(
            id, userTo.getName(), userTo.getEmail(), userTo.getPassword(), true, 0, User.Role.USER);
    userService.save(updated);
    request.logout();
  }

  @DeleteMapping
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Delete authenticated User",
      security = {@SecurityRequirement(name = "basicScheme")})
  public void deleteAuth(
      @AuthenticationPrincipal UserDetailsImpl authUser, HttpServletRequest request)
      throws ServletException {
    log.info("deleteAuth()");
    int id = authUser.getUser().getId();
    AdminUserController.protectAdminPreset(id);
    userService.delete(id);
    request.logout();
  }
}
