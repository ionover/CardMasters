package com.example.cardmasters.docker;

import com.example.cardmasters.dto.Amount;
import com.example.cardmasters.dto.Card;
import com.example.cardmasters.dto.ConfirmRequest;
import com.example.cardmasters.dto.TransferRequest;

public class TestDataFactory {

    // Карты для тестирования
    public static Card createBasicCard() {
        return new Card("1234567890123456", 10000L, "12/25", 123);
    }

    public static Card createDuplicateNumberCard() {
        return new Card("5555666677778888", 10000L, "12/25", 123);
    }

    public static Card createSecondDuplicateCard() {
        return new Card("5555666677778888", 20000L, "01/26", 456);
    }

    public static Card createOriginalCard() {
        return new Card("9876543210987654", 15000L, "06/27", 789);
    }

    public static Card createDuplicateOriginalCard() {
        return new Card("9876543210987654", 50000L, "12/28", 111);
    }

    // Карты для транзакций
    public static Card createSenderCard() {
        return new Card("1111222233334444", 50000L, "12/25", 123);
    }

    public static Card createReceiverCard() {
        return new Card("5555666677778888", 10000L, "06/27", 456);
    }

    public static Card createLowBalanceSenderCard() {
        return new Card("1111222233334444", 5000L, "12/25", 123);
    }

    // Транзакции
    public static TransferRequest createTransferRequest() {
        Amount transferAmount = new Amount(15000, "RUB");
        return new TransferRequest("1111222233334444", "12/25", "123", "5555666677778888", transferAmount);
    }

    public static ConfirmRequest createConfirmRequest(Integer operationId) {
        return new ConfirmRequest(operationId, 777);
    }

    // Константы для проверок
    public static final String SENDER_CARD_NUMBER = "1111222233334444";
    public static final String RECEIVER_CARD_NUMBER = "5555666677778888";
    public static final long INITIAL_SENDER_BALANCE = 50000L;
    public static final long INITIAL_RECEIVER_BALANCE = 10000L;
    public static final long LOW_SENDER_BALANCE = 5000L;
    public static final long TRANSFER_AMOUNT = 15000L;
    public static final long EXPECTED_SENDER_BALANCE_AFTER_TRANSFER = INITIAL_SENDER_BALANCE - TRANSFER_AMOUNT;
    public static final long EXPECTED_RECEIVER_BALANCE_AFTER_TRANSFER = INITIAL_RECEIVER_BALANCE + TRANSFER_AMOUNT;
}
