package com.pafolder.librarian.infrastructure.repository;

import com.pafolder.librarian.domain.model.Checkout;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Integer>,
    com.pafolder.librarian.domain.repository.CheckoutRepository {
  @EntityGraph(attributePaths = {"book"})
  @Query(
      "SELECT c FROM Checkout c WHERE :id = c.user.id AND c.checkinDateTime = NULL ORDER BY"
          + " c.checkoutDateTime")
  List<Checkout> findAllActiveByUserId(int id);

  @EntityGraph(attributePaths = {"user", "book"})
  @Query(
      "SELECT c FROM Checkout c WHERE c.id BETWEEN :fromId AND :toId  ORDER BY c.checkoutDateTime")
  List<Checkout> findAllFromIdToId(int fromId, int toId);

  @EntityGraph(attributePaths = {"user", "book"})
  @Query(
      "SELECT c FROM Checkout c WHERE c.checkinDateTime = NULL AND c.id BETWEEN :fromId AND :toId"
          + " ORDER BY c.checkoutDateTime")
  List<Checkout> findAllActiveFromIdToId(int fromId, int toId);

  @EntityGraph(attributePaths = {"user", "book"})
  @Query(
      "SELECT c FROM Checkout c WHERE c.user.id = :userId AND c.id BETWEEN :fromId AND :toId  ORDER"
          + " BY c.checkoutDateTime")
  List<Checkout> findAllByUserIdFromIdToId(int userId, int fromId, int toId);

  @EntityGraph(attributePaths = {"user", "book"})
  @Query(
      "SELECT c FROM Checkout c WHERE c.user.id = :userId AND c.checkinDateTime = NULL AND c.id"
          + " BETWEEN :fromId AND :toId ORDER BY c.checkoutDateTime")
  List<Checkout> findAllActiveByUserIdFromIdToId(int userId, int fromId, int toId);
}
