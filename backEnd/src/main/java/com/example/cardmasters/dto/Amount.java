package com.example.cardmasters.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Amount {

    @NotNull(message = "Значение суммы должно быть заполнено")
    @Positive(message = "Значение суммы должно быть положительным")
    private Integer value;

    private String currency;

    public Amount() {
        // Конструктор по умолчанию для десериализации JSON
    }

    public Amount(Integer value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return value + " " + currency;
    }
}
