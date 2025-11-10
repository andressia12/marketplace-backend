package com.credibanco.marketplace.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.credibanco.marketplace.Dto.CardDto;
import com.credibanco.marketplace.Service.CardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CardController {
    private final CardService cardService;
    
    @PostMapping
    public ResponseEntity<CardDto.Response> createCard(@Valid @RequestBody CardDto.CreateRequest request) {
        CardDto.Response response = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/recharge")
    public ResponseEntity<CardDto.Response> rechargeCard(@Valid @RequestBody CardDto.RechargeRequest request) {
        CardDto.Response response = cardService.rechargeCard(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/purchase")
    public ResponseEntity<CardDto.Response> processPurchase(@Valid @RequestBody CardDto.PurchaseRequest request) {
        CardDto.Response response = cardService.processPurchase(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<CardDto.Response>> getAllCards() {
        List<CardDto.Response> cards = cardService.getAllCards();
        return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/{cardNumber}")
    public ResponseEntity<CardDto.Response> getCardByNumber(@PathVariable String cardNumber) {
        CardDto.Response card = cardService.getCardByNumber(cardNumber);
        return ResponseEntity.ok(card);
    }
}
