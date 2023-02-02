package com.pafolder.cbr.controller.admin;

import com.pafolder.cbr.model.User;
import com.pafolder.cbr.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.pafolder.cbr.controller.admin.AdminUserController.REST_URL;

@RestController
@AllArgsConstructor
@Tag(name = "admin-users-controller")
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUserController {
    public static final String REST_URL = "/api/admin/users";
    private static final int PRESET_ADMIN_ID = 1;
    private static final String CANT_CHANGE_PRESET_ADMIN = "Changing preset Admin isn't allowed in test mode";
    private final Logger log = LoggerFactory.getLogger(getClass());
    protected UserServiceImpl userService;

    @GetMapping
    @Operation(summary = "Get all users", security = {@SecurityRequirement(name = "basicScheme")})
    public List<User> getAll() {
        log.info("getAll()");
        return userService.getAll();
    }

    @PatchMapping("/{id}/enabled")
    @Operation(summary = "Enable/disable user", security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "id", description = "User's to be enabled/disabled Id")
    public void updateEnabled(@PathVariable int id, @RequestParam boolean isEnabled) {
        log.info("updateEnabled()");
        protectAdminPreset(id);
        userService.updateIsEnabled(id, isEnabled);
    }

    @PatchMapping("/{id}/violations")
    @Operation(summary = "Update user's violation count", security = {@SecurityRequirement(name = "basicScheme")})
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

    public static void protectAdminPreset(int id) {
        if (id == PRESET_ADMIN_ID) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, CANT_CHANGE_PRESET_ADMIN);
        }
    }
}
