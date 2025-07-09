package com.example.cardmasters.services;

import com.example.cardmasters.dto.Card;
import com.example.cardmasters.dto.TransferRequest;
import com.example.cardmasters.exceptions.MoneyException;
import com.example.cardmasters.repository.CardRepos;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransferService {

    private final CardRepos cardRepos;

    public TransferService(CardRepos cardRepos) {
        this.cardRepos = cardRepos;
    }

    public boolean doTransfer(TransferRequest transferRequest) {
        boolean success = false;
        // Получаем карту отправителя
        Optional<Card> fromCardOpt = cardRepos.getCard(transferRequest.getCardFromNumber());
        if (fromCardOpt.isEmpty()) {
            throw new MoneyException("Карта отправителя не найдена");
        }

        Card fromCard = fromCardOpt.get();

        // Валидируем данные карты отправителя
        if (!fromCard.getValidTill().equals(transferRequest.getCardFromValidTill())) {
            throw new MoneyException("Неверный срок действия карты отправителя");
        }

        if (!fromCard.getCvv().toString().equals(transferRequest.getCardFromCVV())) {
            throw new MoneyException("Неверный CVV карты отправителя");
        }

        // Получаем карту получателя
        Optional<Card> toCardOpt = cardRepos.getCard(transferRequest.getCardToNumber());
        if (toCardOpt.isEmpty()) {
            throw new MoneyException("Карта получателя не найдена");
        }

        Card toCard = toCardOpt.get();

        // Проверяем достаточность средств на карте отправителя
        Long transferAmount = transferRequest.getAmount().getValue().longValue();
        if (fromCard.getBalance() < transferAmount) {
            throw new MoneyException("Недостаточно средств на карте отправителя");
        }

        // Выполняем перевод
        fromCard.setBalance(fromCard.getBalance() - transferAmount);
        toCard.setBalance(toCard.getBalance() + transferAmount);
        success = true;

        return success;
    }
}
