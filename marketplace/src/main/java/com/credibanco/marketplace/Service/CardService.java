package com.credibanco.marketplace.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.credibanco.marketplace.Dto.CardDto;
import com.credibanco.marketplace.Exception.BusinessException;
import com.credibanco.marketplace.Model.Card;
import com.credibanco.marketplace.Model.Transaction;
import com.credibanco.marketplace.Repository.CardRepository;
import com.credibanco.marketplace.Repository.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final Random random = new Random();
    
    @Transactional
    public CardDto.Response createCard(CardDto.CreateRequest request) {
        String cardNumber = generateCardNumber(request.getProductId());
        
        if (cardRepository.existsByCardNumber(cardNumber)) {
            throw new BusinessException("Número de tarjeta ya existe");
        }
        
        Card card = Card.builder()
                .cardNumber(cardNumber)
                .productId(request.getProductId())
                .holderName(request.getHolderName())
                .cardType(request.getCardType())
                .expirationDate(LocalDate.now().plusYears(3))
                .balance(BigDecimal.ZERO)
                .active(true)
                .build();
        
        Card savedCard = cardRepository.save(card);
        return mapToResponse(savedCard);
    }
    
    @Transactional
    public CardDto.Response rechargeCard(CardDto.RechargeRequest request) {
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new BusinessException("Tarjeta no encontrada"));
        
        if (!card.getActive()) {
            throw new BusinessException("La tarjeta está inactiva");
        }
        
        card.setBalance(card.getBalance().add(request.getAmount()));
        Card updatedCard = cardRepository.save(card);
        
        // Registrar transacción
        Transaction transaction = Transaction.builder()
                .card(card)
                .amount(request.getAmount())
                .status(Transaction.TransactionStatus.EXITOSA)
                .type(Transaction.TransactionType.RECARGA)
                .description("Recarga de saldo")
                .build();
        transactionRepository.save(transaction);
        
        return mapToResponse(updatedCard);
    }
    
    @Transactional
    public CardDto.Response processPurchase(CardDto.PurchaseRequest request) {
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new BusinessException("Tarjeta no encontrada"));
        
        // Validaciones
        if (!card.getActive()) {
            createFailedTransaction(card, request.getAmount(), "Tarjeta inactiva");
            throw new BusinessException("La tarjeta está inactiva");
        }
        
        if (!card.getHolderName().equalsIgnoreCase(request.getHolderName())) {
            createFailedTransaction(card, request.getAmount(), "Nombre del titular no coincide");
            throw new BusinessException("Nombre del titular no coincide");
        }
        
        LocalDate expiration = parseExpirationDate(request.getExpirationDate());
        if (!card.getExpirationDate().equals(expiration)) {
            createFailedTransaction(card, request.getAmount(), "Fecha de vencimiento no coincide");
            throw new BusinessException("Fecha de vencimiento no coincide");
        }
        
        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            createFailedTransaction(card, request.getAmount(), "Tarjeta vencida");
            throw new BusinessException("Tarjeta vencida");
        }
        
        if (card.getBalance().compareTo(request.getAmount()) < 0) {
            createFailedTransaction(card, request.getAmount(), "Saldo insuficiente");
            throw new BusinessException("Saldo insuficiente");
        }
        
        // Procesar compra
        card.setBalance(card.getBalance().subtract(request.getAmount()));
        Card updatedCard = cardRepository.save(card);
        
        Transaction transaction = Transaction.builder()
                .card(card)
                .amount(request.getAmount())
                .status(Transaction.TransactionStatus.EXITOSA)
                .type(Transaction.TransactionType.COMPRA)
                .description(request.getDescription())
                .build();
        transactionRepository.save(transaction);
        
        return mapToResponse(updatedCard);
    }
    
    public List<CardDto.Response> getAllCards() {
        return cardRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public CardDto.Response getCardByNumber(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new BusinessException("Tarjeta no encontrada"));
        return mapToResponse(card);
    }
    
    private void createFailedTransaction(Card card, BigDecimal amount, String reason) {
        Transaction transaction = Transaction.builder()
                .card(card)
                .amount(amount)
                .status(Transaction.TransactionStatus.RECHAZADA)
                .type(Transaction.TransactionType.COMPRA)
                .description(reason)
                .build();
        transactionRepository.save(transaction);
    }
    
    private String generateCardNumber(String productId) {
        StringBuilder cardNumber = new StringBuilder(productId);
        for (int i = 0; i < 10; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }
    
    private LocalDate parseExpirationDate(String expirationDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            String[] parts = expirationDate.split("/");
            return LocalDate.of(Integer.parseInt(parts[1]), Integer.parseInt(parts[0]), 1);
        } catch (Exception e) {
            throw new BusinessException("Formato de fecha inválido");
        }
    }
    
    private CardDto.Response mapToResponse(Card card) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        return CardDto.Response.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .productId(card.getProductId())
                .holderName(card.getHolderName())
                .expirationDate(card.getExpirationDate().format(formatter))
                .cardType(card.getCardType())
                .balance(card.getBalance())
                .active(card.getActive())
                .createdAt(card.getCreatedAt())
                .build();
    }
}
