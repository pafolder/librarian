package com.pafolder.cbr.repository;

import com.pafolder.cbr.model.User;
import com.pafolder.cbr.model.Checkout;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Transactional(readOnly = true)
public interface CheckoutRepository extends JpaRepository<Checkout, Integer> {
    @EntityGraph(attributePaths = {"user", "book"})
    @Query("SELECT c FROM Checkout c WHERE :user=c.user AND c.checkinDateTime=NULL")
    List<Checkout> findAllByUser(User user);
}
