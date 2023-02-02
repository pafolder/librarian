package com.pafolder.cbr.controller;

import com.pafolder.cbr.model.User;
import com.pafolder.cbr.service.UserServiceImpl;
import com.pafolder.cbr.to.UserTo;
import com.pafolder.cbr.validator.UserToValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "register-controller")
@RequestMapping(value = RegisterController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegisterController {
    public static final String REST_URL = "/api/register";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private UserServiceImpl userService;
    protected UserToValidator userToValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(userToValidator);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user", security = {@SecurityRequirement(name = "basicScheme")})
    public ResponseEntity<User> register(@Valid @RequestBody UserTo userTo) {
        log.info("register()");
        User created = userService.save(new User(null,
                userTo.getName(), userTo.getEmail(), userTo.getPassword(), true, 0, User.Role.USER));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}
