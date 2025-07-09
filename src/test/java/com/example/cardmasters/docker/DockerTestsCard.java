package com.example.cardmasters.docker;

import com.example.cardmasters.dto.Card;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DockerTestsCard extends BaseDockerTest {

    @Test
    void canCreateCard() {
        // 1. Сначала делаем GET /cards - должен вернуть пустой список []
        assertCardsCount(0);

        // 2. Создаем новую карту через POST /addCard
        Card newCard = TestDataFactory.createBasicCard();
        assertCardCreatedSuccessfully(newCard);

        // 3. Снова делаем GET /cards - теперь должен вернуть созданную карту
        assertCardsCount(1);
    }

    @Test
    void canNotCreateSameNumberCard() {
        // 1. Создаем первую карту
        Card firstCard = TestDataFactory.createDuplicateNumberCard();
        assertCardCreatedSuccessfully(firstCard);

        // 2. Пытаемся создать вторую карту с тем же номером
        Card secondCard = TestDataFactory.createSecondDuplicateCard();

        // 3. Ожидаем получить статус 400 (Bad Request)
        assertCardCreationFailed(secondCard, 400);
    }

    @Test
    void cardDoesNotChangeWhenCreatingSameNumber() {
        // 1. Создаем первую карту с определенными параметрами
        Card originalCard = TestDataFactory.createOriginalCard();
        assertCardCreatedSuccessfully(originalCard);

        // 2. Получаем список карт и проверяем исходную карту
        assertCardsCount(1);
        Card[] cards = getAllCards().getBody();
        Card retrievedOriginalCard = cards[0];
        assertEquals("9876543210987654", retrievedOriginalCard.getNumber());
        assertEquals(15000L, retrievedOriginalCard.getBalance());
        assertEquals(789, retrievedOriginalCard.getCvv());

        // 3. Пытаемся создать карту с тем же номером, но другими параметрами
        Card duplicateCard = TestDataFactory.createDuplicateOriginalCard();
        assertCardCreationFailed(duplicateCard, 400);

        // 4. Проверяем, что исходная карта не изменилась
        assertCardsCount(1);
        Card[] finalCards = getAllCards().getBody();
        Card unchangedCard = finalCards[0];
        assertEquals("9876543210987654", unchangedCard.getNumber());
        assertEquals(15000L, unchangedCard.getBalance()); // Баланс не изменился
        assertEquals(789, unchangedCard.getCvv()); // CVV не изменился
    }
}
