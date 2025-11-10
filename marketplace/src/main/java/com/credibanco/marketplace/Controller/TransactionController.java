package com.credibanco.marketplace.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.credibanco.marketplace.Dto.TransactionDto;
import com.credibanco.marketplace.Service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {
    private final TransactionService transactionService;
    
    @GetMapping
    public ResponseEntity<List<TransactionDto.Response>> getAllTransactions() {
        List<TransactionDto.Response> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<List<TransactionDto.Response>> getTransactionsByCard(@PathVariable String cardNumber) {
        List<TransactionDto.Response> transactions = transactionService.getTransactionsByCard(cardNumber);
        return ResponseEntity.ok(transactions);
    }
    
    @PostMapping("/{transactionId}/cancel")
    public ResponseEntity<TransactionDto.Response> cancelTransaction(@PathVariable Long transactionId) {
        TransactionDto.Response response = transactionService.cancelTransaction(transactionId);
        return ResponseEntity.ok(response);
    }
}
