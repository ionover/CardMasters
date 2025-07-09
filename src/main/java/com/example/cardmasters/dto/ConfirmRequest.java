package com.example.cardmasters.dto;

import jakarta.validation.constraints.NotNull;

public class ConfirmRequest {

    @NotNull(message = "ID операции должен быть заполнен")
    private int operationId;

    @NotNull(message = "Код должен быть заполнен")
    private int code;

    public ConfirmRequest() {
        // Конструктор по умолчанию для десериализации JSON
    }

    public ConfirmRequest(int operationId, int code) {
        this.operationId = operationId;
        this.code = code;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
