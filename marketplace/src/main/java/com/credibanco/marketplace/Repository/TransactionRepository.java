package com.credibanco.marketplace.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.credibanco.marketplace.Model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCardIdOrderByCreatedAtDesc(Long cardId);
    List<Transaction> findAllByOrderByCreatedAtDesc();
}
