package com.pafolder.cbr.controller.admin;

import com.pafolder.cbr.controller.profile.CheckoutController;
import com.pafolder.cbr.model.Checkout;
import com.pafolder.cbr.repository.CheckoutRepository;
import com.pafolder.cbr.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@Tag(name = "admin-checkout-controller")
@RequestMapping(value = CheckoutController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminCheckoutController {
    public static final String NO_CHECKOUT_FOUND = "No checkout found";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private CheckoutRepository checkoutRepository;

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete authenticated user's borrowing (ex. Book is lost)",
            security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "id", description = "Checkout Id")
    @Transactional
    public void delete(@RequestParam int id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("delete");
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_CHECKOUT_FOUND));
        checkoutRepository.delete(checkout);
    }
}
