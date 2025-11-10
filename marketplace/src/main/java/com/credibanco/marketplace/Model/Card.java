package com.credibanco.marketplace.Model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 16)
    @Size(min = 16, max = 16)
    private String cardNumber;
    
    @Column(nullable = false, length = 6)
    private String productId;
    
    @Column(nullable = false)
    @NotBlank(message = "El nombre del titular es requerido")
    private String holderName;
    
    @Column(nullable = false)
    private LocalDate expirationDate;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;
    
    @Column(nullable = false, precision = 15, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal balance;
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        this.balance = BigDecimal.ZERO;
        this.active = true;
    }
    
    public enum CardType {
        CREDITO,
        DEBITO
    }
}
