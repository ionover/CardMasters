package com.example.cardmasters.docker;

import com.example.cardmasters.dto.ConfirmRequest;
import com.example.cardmasters.dto.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestsTransaction extends BaseDockerTest {

    private TransactionTestHelper transactionHelper;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        transactionHelper = new TransactionTestHelper(restTemplate, baseUrl, headers);
    }

    @Test
    void canCreateMoneyTransfer() {
        // 1. Создаем две карты для перевода
        transactionHelper.setupTwoCardsForTransfer();

        // 2. Выполняем перевод
        TransferRequest transferRequest = TestDataFactory.createTransferRequest();

        // 3. Проверяем, что балансы НЕ изменились (нет подтверждения)
        assertCardBalance(TestDataFactory.SENDER_CARD_NUMBER, TestDataFactory.INITIAL_SENDER_BALANCE);
        assertCardBalance(TestDataFactory.RECEIVER_CARD_NUMBER, TestDataFactory.INITIAL_RECEIVER_BALANCE);
    }

    @Test
    void successfulPayment() {
        // 1. Создаем две карты для перевода
        transactionHelper.setupTwoCardsForTransfer();

        // 2. Выполняем перевод
        TransferRequest transferRequest = TestDataFactory.createTransferRequest();
        Integer operationId = transactionHelper.executeTransfer(transferRequest);

        // 3. Подтверждаем операцию
        ConfirmRequest confirmRequest = TestDataFactory.createConfirmRequest(operationId);
        ResponseEntity<Integer> confirmResponse = transactionHelper.confirmOperation(confirmRequest);
        assertEquals(200, confirmResponse.getStatusCodeValue());

        // 4. Проверяем, что балансы изменились после подтверждения
        assertCardBalance(TestDataFactory.SENDER_CARD_NUMBER, TestDataFactory.EXPECTED_SENDER_BALANCE_AFTER_TRANSFER);
        assertCardBalance(TestDataFactory.RECEIVER_CARD_NUMBER,
                          TestDataFactory.EXPECTED_RECEIVER_BALANCE_AFTER_TRANSFER);
    }

    @Test
    void transferInsufficientFunds() {
        // 1. Создаем две карты: отправитель с низким балансом и получатель
        transactionHelper.setupTwoCardsWithLowSenderBalance();

        // 2. Пытаемся перевести больше, чем есть на карте (15000 > 5000)
        TransferRequest transferRequest = TestDataFactory.createTransferRequest();
        Integer operationId = transactionHelper.executeTransfer(transferRequest);

        // 3. Подтверждаем операцию (ожидаем ошибку из-за недостатка средств)
        ConfirmRequest confirmRequest = TestDataFactory.createConfirmRequest(operationId);
        ResponseEntity<String> confirmResponse = transactionHelper.confirmOperationExpectingError(confirmRequest);
        assertEquals(400, confirmResponse.getStatusCodeValue());
        assertEquals("Недостаточно средств на карте отправителя", confirmResponse.getBody());

        // 4. Проверяем, что балансы НЕ изменились (недостаточно средств)
        assertCardBalance(TestDataFactory.SENDER_CARD_NUMBER, TestDataFactory.LOW_SENDER_BALANCE);
        assertCardBalance(TestDataFactory.RECEIVER_CARD_NUMBER, TestDataFactory.INITIAL_RECEIVER_BALANCE);
    }
}
