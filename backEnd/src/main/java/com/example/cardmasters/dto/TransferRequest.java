package com.example.cardmasters.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TransferRequest {

    @NotBlank(message = "Номер карты отправителя должен быть заполнен")
    private String cardFromNumber;

    @NotBlank(message = "Срок действия карты отправителя должен быть заполнен")
    private String cardFromValidTill;

    @NotBlank(message = "CVV карты отправителя должен быть заполнен")
    private String cardFromCVV;

    @NotBlank(message = "Номер карты получателя должен быть заполнен")
    private String cardToNumber;

    @NotNull(message = "Сумма перевода должна быть заполнена")
    @Valid
    private Amount amount;

    public TransferRequest() {
        // Конструктор по умолчанию для десериализации JSON
    }

    public TransferRequest(String cardFromNumber, String cardFromValidTill, String cardFromCVV, String cardToNumber,
                           Amount amount) {
        this.cardFromNumber = cardFromNumber;
        this.cardFromValidTill = cardFromValidTill;
        this.cardFromCVV = cardFromCVV;
        this.cardToNumber = cardToNumber;
        this.amount = amount;
    }

    public String getCardFromNumber() {
        return cardFromNumber;
    }

    public void setCardFromNumber(String cardFromNumber) {
        this.cardFromNumber = cardFromNumber;
    }

    public String getCardFromValidTill() {
        return cardFromValidTill;
    }

    public void setCardFromValidTill(String cardFromValidTill) {
        this.cardFromValidTill = cardFromValidTill;
    }

    public String getCardFromCVV() {
        return cardFromCVV;
    }

    public void setCardFromCVV(String cardFromCVV) {
        this.cardFromCVV = cardFromCVV;
    }

    public String getCardToNumber() {
        return cardToNumber;
    }

    public void setCardToNumber(String cardToNumber) {
        this.cardToNumber = cardToNumber;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}
