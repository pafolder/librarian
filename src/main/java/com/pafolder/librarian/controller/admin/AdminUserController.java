package com.pafolder.librarian.controller.admin;

import com.pafolder.librarian.model.User;
import com.pafolder.librarian.repository.CheckoutRepository;
import com.pafolder.librarian.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.pafolder.librarian.controller.admin.AdminUserController.REST_URL;
import static com.pafolder.librarian.controller.profile.CheckoutController.getFilteredCheckoutsJson;
import static com.pafolder.librarian.controller.profile.CheckoutController.getFutureViolations;

@RestController
@AllArgsConstructor
@Tag(name = "7 admin-user-controller")
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUserController {
    public static final String REST_URL = "/api/admin/users";
    private static final int PRESET_ADMIN_ID = 1;
    private static final String CANT_CHANGE_PRESET_ADMIN = "Changing preset Admin isn't allowed in test mode";
    private final Logger log = LoggerFactory.getLogger(getClass());
    protected UserServiceImpl userService;
    private CheckoutRepository checkoutRepository;

    @GetMapping
    @Operation(summary = "Get all users with Ids between fromId and toId", security = {@SecurityRequirement(name = "basicScheme")})
    public List<User> getAllFromIdToId(@RequestParam(defaultValue = "1") int fromId, @RequestParam @Nullable Integer toId) {
        log.info("getAllFromIdToId()");
        List<User> users = userService.getAllFromIdToId(fromId, Optional.ofNullable(toId).orElse(0));
        users.forEach(user -> user.setViolations(getFutureViolations(user, checkoutRepository)));
        return users;
    }

    @PatchMapping("/{id}/enabled")
    @Operation(summary = "Enable/disable user", security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "id", description = "User's to be enabled/disabled Id")
    public void updateEnabled(@PathVariable int id, @RequestParam(defaultValue = "true") boolean isEnabled) {
        log.info("updateEnabled()");
        protectAdminPreset(id);
        userService.updateIsEnabled(id, isEnabled);
    }

    @PatchMapping("/{id}/violations")
    @Operation(summary = "Update user's violations count", security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "id", description = "User's Id to be processed")
    public void updateViolations(@PathVariable int id, @RequestParam int violations) {
        log.info("updateViolations()");
        userService.updateViolations(id, violations);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user", security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "id", description = "User to be deleted Id")
    public void delete(@PathVariable int id) {
        log.info("delete()");
        protectAdminPreset(id);
        userService.delete(id);
    }

    @GetMapping("/{id}/checkouts")
    @Operation(summary = "Get active or all checkouts with Ids between fromId and toId",
            security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "isActive", description = "true - for active checkouts (no checkin yet)")
    public MappingJacksonValue getAllCheckoutsFromIdToId(@PathVariable int id,
                                                         @RequestParam(defaultValue = "1") int fromId,
                                                         @RequestParam @Nullable Integer toId,
                                                         @RequestParam(defaultValue = "true") boolean isActive) {
        log.info("getAllCheckoutsFromIdToId()");
        return getFilteredCheckoutsJson(isActive ?
                checkoutRepository.findAllActiveByUserIdFromIdToId(id, fromId, Optional.ofNullable(toId)
                        .orElse(0)) :
                checkoutRepository.findAllByUserIdFromIdToId(id, fromId, Optional.ofNullable(toId).orElse(0)));
    }

    public static void protectAdminPreset(int id) {
        if (id == PRESET_ADMIN_ID) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, CANT_CHANGE_PRESET_ADMIN);
        }
    }
}
