package com.pafolder.librarian.infrastructure.controller.admin;

import static com.pafolder.librarian.infrastructure.controller.admin.AdminCheckoutController.REST_URL;
import static com.pafolder.librarian.infrastructure.controller.ControllerUtil.getFilteredCheckoutsJson;
import static com.pafolder.librarian.infrastructure.controller.ControllerUtil.getFutureViolations;

import com.pafolder.librarian.domain.model.Checkout;
import com.pafolder.librarian.infrastructure.repository.CheckoutRepository;
import com.pafolder.librarian.infrastructure.controller.view.CheckoutView;
import com.pafolder.librarian.infrastructure.security.UserDetailsImpl;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@Tag(name = "6 admin-checkout-controller")
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminCheckoutController {
  public static final String REST_URL = "/api/admin/checkouts";
  public static final String NO_CHECKOUT_FOUND = "No checkout found";
  private final Logger log = LoggerFactory.getLogger(getClass());
  private CheckoutRepository checkoutRepository;

  @GetMapping()
  @Operation(
      summary = "Get active or all checkouts with Ids between fromId and toId",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "isActive", description = "True - for getting only active checkouts")
  @Parameter(name = "fromId", description = "From Id")
  @Parameter(name = "toId", description = "To Id")
  public MappingJacksonValue getAllFromIdToId(
      @RequestParam(defaultValue = "1") int fromId,
      @RequestParam @Nullable Integer toId,
      @RequestParam(defaultValue = "true") boolean isActive) {
    log.info("getAllFromIdToId()");
    List<Checkout> checkouts =
        isActive
            ? checkoutRepository.findAllActiveFromIdToId(
                fromId, Optional.ofNullable(toId).orElse(0))
            : checkoutRepository.findAllFromIdToId(fromId, Optional.ofNullable(toId).orElse(0));
    checkouts.forEach(
        ch -> ch.getUser().setViolations(getFutureViolations(ch.getUser())));
    return getFilteredCheckoutsJson(true, checkouts.stream()
        .map(CheckoutView::new).collect(Collectors.toList()));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Delete checkout",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "id", description = "Checkout Id")
  @Transactional
  public void delete(@PathVariable int id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    log.info("delete");
    Checkout checkout =
        checkoutRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, NO_CHECKOUT_FOUND));
    checkoutRepository.delete(checkout);
  }
}
