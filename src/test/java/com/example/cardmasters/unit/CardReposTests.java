package com.example.cardmasters.unit;

import com.example.cardmasters.dto.Card;
import com.example.cardmasters.repository.CardRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CardReposTests {

    private CardRepos cardRepos;

    @BeforeEach
    void setUp() {
        cardRepos = new CardRepos();
    }

    @Test
    void addCard_ValidCard_SuccessfullyAddsAndReturnsTrue() {
        Card card = new Card("1111222233334444", 10000L, "12/25", 123);

        boolean result = cardRepos.addCard(card);
        Optional<Card> retrievedCard = cardRepos.getCard("1111222233334444");

        assertTrue(result);
        assertTrue(retrievedCard.isPresent());
        assertEquals("1111222233334444", retrievedCard.get().getNumber());
        assertEquals(10000L, retrievedCard.get().getBalance());
        assertEquals("12/25", retrievedCard.get().getValidTill());
        assertEquals(123, retrievedCard.get().getCvv());
        assertEquals(1, cardRepos.getCardCount());
    }

    @Test
    void addCard_DuplicateCardNumber_ReturnsFalseAndDoesNotOverwrite() {
        Card firstCard = new Card("1111222233334444", 10000L, "12/25", 123);
        Card duplicateCard = new Card("1111222233334444", 5000L, "06/26", 456);

        boolean firstResult = cardRepos.addCard(firstCard);
        boolean duplicateResult = cardRepos.addCard(duplicateCard);
        Optional<Card> retrievedCard = cardRepos.getCard("1111222233334444");

        assertTrue(firstResult);
        assertFalse(duplicateResult);
        assertTrue(retrievedCard.isPresent());
        
        assertEquals(10000L, retrievedCard.get().getBalance());
        assertEquals("12/25", retrievedCard.get().getValidTill());
        assertEquals(123, retrievedCard.get().getCvv());
        assertEquals(1, cardRepos.getCardCount());
    }
}
