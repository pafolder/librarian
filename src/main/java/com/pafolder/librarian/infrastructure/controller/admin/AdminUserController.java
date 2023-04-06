package com.pafolder.librarian.infrastructure.controller.admin;

import static com.pafolder.librarian.infrastructure.controller.admin.AdminUserController.REST_URL;
import static com.pafolder.librarian.infrastructure.controller.ControllerUtil.getFilteredCheckoutsJson;
import static com.pafolder.librarian.infrastructure.controller.ControllerUtil.getFutureViolations;

import com.pafolder.librarian.domain.model.User;
import com.pafolder.librarian.domain.repository.CheckoutRepository;
import com.pafolder.librarian.domain.service.UserService;
import com.pafolder.librarian.infrastructure.controller.view.CheckoutView;
import com.pafolder.librarian.infrastructure.controller.view.UserView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@Tag(name = "7 admin-user-controller")
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUserController {
  public static final String REST_URL = "/api/admin/users";
  private static final int PRESET_ADMIN_ID = 1;
  private static final String CANT_CHANGE_PRESET_ADMIN =
      "Changing preset Admin isn't allowed in test mode";
  private final Logger log = LoggerFactory.getLogger(getClass());
  protected UserService userService;
  private CheckoutRepository checkoutRepository;

  @GetMapping
  @Operation(
      summary = "Get all users with Ids between fromId and toId",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "fromId", description = "From Id")
  @Parameter(name = "toId", description = "To Id")
  public List<UserView> getAllFromIdToId(
      @RequestParam(defaultValue = "1") int fromId, @RequestParam @Nullable Integer toId) {
    log.info("getAllFromIdToId()");
    List<User> users = userService.getAllFromIdToId(fromId, Optional.ofNullable(toId).orElse(0));
    users.forEach(user -> user.setViolations(getFutureViolations(user)));
    return users.stream().map(UserView::new).collect(Collectors.toList());
  }

  @PatchMapping("/{id}/enabled")
  @Operation(
      summary = "Enable/disable user",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "id", description = "User's to be enabled/disabled Id")
  public void updateEnabled(
      @PathVariable int id, @RequestParam(defaultValue = "true") boolean isEnabled) {
    log.info("updateEnabled()");
    protectAdminPreset(id);
    userService.updateIsEnabled(id, isEnabled);
  }

  @PatchMapping("/{id}/violations")
  @Operation(
      summary = "Update user's violations count",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "id", description = "User's Id to be processed")
  public void updateViolations(@PathVariable int id, @RequestParam int violations) {
    log.info("updateViolations()");
    userService.updateViolations(id, violations);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Delete user",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "id", description = "User to be deleted Id")
  public void delete(@PathVariable int id) {
    log.info("delete()");
    protectAdminPreset(id);
    userService.delete(id);
  }

  @GetMapping("/{id}/checkouts")
  @Operation(
      summary = "Get active or all checkouts with Ids between fromId and toId",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "isActive", description = "true - for active checkouts (no checkin yet)")
  public MappingJacksonValue getAllCheckoutsFromIdToId(
      @PathVariable int id,
      @RequestParam(defaultValue = "1") int fromId,
      @RequestParam @Nullable Integer toId,
      @RequestParam(defaultValue = "true") boolean isActive) {
    log.info("getAllCheckoutsFromIdToId()");
    return getFilteredCheckoutsJson(
        true,
        isActive
            ? checkoutRepository.findAllActiveByUserIdFromIdToId(
                id, fromId, Optional.ofNullable(toId).orElse(0))
            .stream().map(CheckoutView::new).collect(Collectors.toList())
            : checkoutRepository.findAllByUserIdFromIdToId(
                id, fromId, Optional.ofNullable(toId).orElse(0))
            .stream().map(CheckoutView::new).collect(Collectors.toList()));
  }

  public static void protectAdminPreset(int id) {
    if (id == PRESET_ADMIN_ID) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, CANT_CHANGE_PRESET_ADMIN);
    }
  }
}
