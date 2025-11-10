package com.credibanco.marketplace.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.credibanco.marketplace.Model.Card;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CardDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "El productId es requerido")
        @Size(min = 6, max = 6, message = "El productId debe tener 6 dígitos")
        @Pattern(regexp = "\\d{6}", message = "El productId debe ser numérico")
        private String productId;
        
        @NotBlank(message = "El nombre del titular es requerido")
        @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+\\s[A-Za-zÁÉÍÓÚáéíóúÑñ]+$", 
                message = "Debe incluir primer nombre y apellido")
        private String holderName;
        
        @NotNull(message = "El tipo de tarjeta es requerido")
        private Card.CardType cardType;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RechargeRequest {
        @NotBlank(message = "El número de tarjeta es requerido")
        @Size(min = 16, max = 16)
        private String cardNumber;
        
        @NotNull(message = "El monto es requerido")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
        private BigDecimal amount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PurchaseRequest {
        @NotBlank(message = "El nombre del titular es requerido")
        private String holderName;
        
        @NotBlank(message = "El número de tarjeta es requerido")
        @Size(min = 16, max = 16)
        private String cardNumber;
        
        @NotBlank(message = "La fecha de vencimiento es requerida")
        @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{4}$", 
                message = "Formato debe ser MM/YYYY")
        private String expirationDate;
        
        @NotNull(message = "El monto es requerido")
        @DecimalMin(value = "0.01")
        private BigDecimal amount;
        
        private String description;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String cardNumber;
        private String productId;
        private String holderName;
        private String expirationDate;
        private Card.CardType cardType;
        private BigDecimal balance;
        private Boolean active;
        private LocalDate createdAt;
    }
}
