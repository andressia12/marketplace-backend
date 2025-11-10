package com.credibanco.marketplace.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.credibanco.marketplace.Dto.TransactionDto;
import com.credibanco.marketplace.Exception.BusinessException;
import com.credibanco.marketplace.Model.Card;
import com.credibanco.marketplace.Model.Transaction;
import com.credibanco.marketplace.Repository.CardRepository;
import com.credibanco.marketplace.Repository.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    
    public List<TransactionDto.Response> getAllTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TransactionDto.Response> getTransactionsByCard(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new BusinessException("Tarjeta no encontrada"));
        
        return transactionRepository.findByCardIdOrderByCreatedAtDesc(card.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public TransactionDto.Response cancelTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transacción no encontrada"));
        
        // Validaciones
        if (transaction.getStatus() == Transaction.TransactionStatus.ANULADA) {
            throw new BusinessException("La transacción ya está anulada");
        }
        
        if (transaction.getStatus() == Transaction.TransactionStatus.RECHAZADA) {
            throw new BusinessException("No se puede anular una transacción rechazada");
        }
        
        if (transaction.getType() != Transaction.TransactionType.COMPRA) {
            throw new BusinessException("Solo se pueden anular transacciones de compra");
        }
        
        // Validar que no hayan pasado más de 24 horas
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(transaction.getCreatedAt(), now);
        if (duration.toHours() > 24) {
            throw new BusinessException("La transacción no puede anularse después de 24 horas");
        }
        
        // Anular transacción y devolver saldo
        Card card = transaction.getCard();
        card.setBalance(card.getBalance().add(transaction.getAmount()));
        cardRepository.save(card);
        
        transaction.setStatus(Transaction.TransactionStatus.ANULADA);
        transaction.setCancelledAt(now);
        Transaction cancelledTransaction = transactionRepository.save(transaction);
        
        // Crear registro de anulación
        Transaction cancellationRecord = Transaction.builder()
                .card(card)
                .amount(transaction.getAmount())
                .status(Transaction.TransactionStatus.EXITOSA)
                .type(Transaction.TransactionType.ANULACION)
                .description("Anulación de transacción #" + transactionId)
                .build();
        transactionRepository.save(cancellationRecord);
        
        return mapToResponse(cancelledTransaction);
    }
    
    private TransactionDto.Response mapToResponse(Transaction transaction) {
        return TransactionDto.Response.builder()
                .id(transaction.getId())
                .cardNumber(transaction.getCard().getCardNumber())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .cancelledAt(transaction.getCancelledAt())
                .build();
    }
}
