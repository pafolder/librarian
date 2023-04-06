package com.pafolder.librarian.domain.service;

import com.pafolder.librarian.domain.model.Checkout;

public interface CheckoutService {

  Checkout checkout(int bookId, int userId);

  void checkin(int checkoutId, int userId);

}
