package com.example.cardmasters.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Card {

    @NotBlank(message = "Номер карты должен быть заполнен")
    private String number;

    @NotNull(message = "Баланс карты должен быть заполнен")
    private Long balance;

    @NotBlank(message = "Срок карты должен быть заполнен")
    private String validTill;

    @NotNull(message = "CVV карты должен быть заполнен")
    private Integer cvv;

    public Card() {
        // Конструктор по умолчанию для десериализации JSON
    }

    public Card(String number, Long balance, String validTill, Integer cvv) {
        this.number = number;
        this.balance = balance;
        this.validTill = validTill;
        this.cvv = cvv;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getValidTill() {
        return validTill;
    }

    public void setValidTill(String validTill) {
        this.validTill = validTill;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(Integer cvv) {
        this.cvv = cvv;
    }
}
