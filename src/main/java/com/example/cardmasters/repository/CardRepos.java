package com.example.cardmasters.repository;

import com.example.cardmasters.dto.Card;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CardRepos {

    private final ConcurrentHashMap<String, Card> cardStorage = new ConcurrentHashMap<>();

    public boolean addCard(Card card) {
        if (card == null || card.getNumber() == null) {
            throw new IllegalArgumentException("Карта и номер карты не могут быть null");
        }

        return cardStorage.putIfAbsent(card.getNumber(), card) == null;
    }

    public Optional<Card> getCard(String cardNumber) {
        if (cardNumber == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cardStorage.get(cardNumber));
    }

    public Collection<Card> getAllCards() {
        return cardStorage.values();
    }


    public int getCardCount() {
        return cardStorage.size();
    }


    public void clearAll() {
        cardStorage.clear();
    }
}
