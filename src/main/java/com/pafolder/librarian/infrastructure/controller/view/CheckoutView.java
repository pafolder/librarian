package com.pafolder.librarian.infrastructure.controller.view;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pafolder.librarian.domain.model.Checkout;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@JsonInclude(Include.NON_NULL)
@JsonFilter("checkoutJsonFilter")
@RequiredArgsConstructor
public class CheckoutView {

  private final Checkout checkout;

  public Integer getId() {
    return checkout.getId();
  }

  public UserView getUser() {
    return new UserView(checkout.getUser());
  }

  public BookView getBook() {
    return new BookView(checkout.getBook());
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  public LocalDateTime getCheckoutDateTime() {
    return checkout.getCheckoutDateTime();
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
  public LocalDateTime getCheckinDateTime() {
    return checkout.getCheckinDateTime();
  }


}
