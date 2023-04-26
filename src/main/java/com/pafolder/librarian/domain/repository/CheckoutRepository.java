package com.pafolder.librarian.domain.repository;

import com.pafolder.librarian.domain.model.Checkout;
import java.util.List;
import java.util.Optional;

public interface CheckoutRepository {

  Checkout save(Checkout checkout);

  Optional<Checkout> findById(int id);
  List<Checkout> findAllActiveByUserId(int id);

  List<Checkout> findAllFromIdToId(int fromId, int toId);

  List<Checkout> findAllActiveFromIdToId(int fromId, int toId);

  List<Checkout> findAllByUserIdFromIdToId(int userId, int fromId, int toId);

  List<Checkout> findAllActiveByUserIdFromIdToId(int userId, int fromId, int toId);


}
