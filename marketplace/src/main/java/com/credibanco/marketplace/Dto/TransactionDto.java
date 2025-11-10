package com.credibanco.marketplace.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.credibanco.marketplace.Model.Transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TransactionDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String cardNumber;
        private BigDecimal amount;
        private Transaction.TransactionStatus status;
        private Transaction.TransactionType type;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime cancelledAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CancelRequest {
        private Long transactionId;
    }
}
