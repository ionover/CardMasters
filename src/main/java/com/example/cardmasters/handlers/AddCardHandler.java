package com.example.cardmasters.handlers;

import com.example.cardmasters.dto.Card;
import com.example.cardmasters.repository.CardRepos;
import com.example.cardmasters.logs.LogWriter;
import com.example.cardmasters.exceptions.MoneyException;
import org.springframework.stereotype.Service;

@Service
public class AddCardHandler {

    private final CardRepos cardRepos;
    private final LogWriter logWriter;

    public AddCardHandler(CardRepos cardRepos, LogWriter logWriter) {
        this.cardRepos = cardRepos;
        this.logWriter = logWriter;
    }

    public void handle(Card card) {
        if (card == null) {
            throw new MoneyException("Карта не может быть null");
        }

        boolean added = cardRepos.addCard(card);
        if (!added) {
            logWriter.addCardLog(card, false);
            throw new MoneyException("Карта с номером " + card.getNumber() + " уже существует");
        }
        logWriter.addCardLog(card, true);
    }
}
