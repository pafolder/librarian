package com.pafolder.librarian.repository;

import com.pafolder.librarian.model.Checkout;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Transactional(readOnly = true)
public interface CheckoutRepository extends JpaRepository<Checkout, Integer> {
    @EntityGraph(attributePaths = {"book"})
    @Query("SELECT c FROM Checkout c WHERE :id = c.user.id AND c.checkinDateTime = NULL ORDER BY c.checkoutDateTime")
    List<Checkout> findAllActiveByUserId(int id);

    @EntityGraph(attributePaths = {"user", "book"})
    @Query("SELECT c FROM Checkout c WHERE c.id BETWEEN :fromId AND :toId  ORDER BY c.checkoutDateTime")
    List<Checkout> findAllFromIdToId(int fromId, int toId);

    @EntityGraph(attributePaths = {"user", "book"})
    @Query("SELECT c FROM Checkout c WHERE c.checkinDateTime = NULL AND c.id BETWEEN :fromId AND :toId ORDER BY c.checkoutDateTime")
    List<Checkout> findAllActiveFromIdToId(int fromId, int toId);

    @EntityGraph(attributePaths = {"user", "book"})
    @Query("SELECT c FROM Checkout c WHERE c.user.id = :userId AND c.id BETWEEN :fromId AND :toId  ORDER BY c.checkoutDateTime")
    List<Checkout> findAllByUserIdFromIdToId(int userId, int fromId, int toId);

    @EntityGraph(attributePaths = {"user", "book"})
    @Query("SELECT c FROM Checkout c WHERE c.user.id = :userId AND c.checkinDateTime = NULL AND c.id BETWEEN :fromId AND :toId ORDER BY c.checkoutDateTime")
    List<Checkout> findAllActiveByUserIdFromIdToId(int userId, int fromId, int toId);
}
