package com.example.cardmasters.docker;

import com.example.cardmasters.dto.Card;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DockerTestsCard {

    @Autowired
    private TestRestTemplate restTemplate;

    private GenericContainer<?> createAndStartContainer() {
        GenericContainer<?> container = new GenericContainer<>("devapp:latest")
                .withExposedPorts(8085)
                .waitingFor(Wait.forHttp("/cards").forStatusCode(200));
        container.start();

        return container;
    }

    @Test
    void canCreateCard() {
        GenericContainer<?> devApp = createAndStartContainer();
        try {
            int devPort = devApp.getMappedPort(8085);
            String baseUrl = "http://localhost:" + devPort;

            // 1. Сначала делаем GET /cards - должен вернуть пустой список []
            ResponseEntity<Card[]> initialResponse = restTemplate.getForEntity(
                baseUrl + "/cards", Card[].class);
            assertEquals(200, initialResponse.getStatusCodeValue());
            assertNotNull(initialResponse.getBody());
            assertEquals(0, initialResponse.getBody().length);

            // 2. Создаем новую карту через POST /addCard
            Card newCard = new Card("1234567890123456", 10000L, "12/25", 123);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Card> request = new HttpEntity<>(newCard, headers);

            ResponseEntity<Void> postResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", request, Void.class);
            assertEquals(200, postResponse.getStatusCodeValue());

            // 3. Снова делаем GET /cards - теперь должен вернуть созданную карту
            ResponseEntity<Card[]> finalResponse = restTemplate.getForEntity(
                baseUrl + "/cards", Card[].class);
            assertEquals(200, finalResponse.getStatusCodeValue());
            assertNotNull(finalResponse.getBody());
            assertEquals(1, finalResponse.getBody().length);
        } finally {
            devApp.stop();
        }
    }

    @Test
    void canNotCreateSameNumberCard() {
        GenericContainer<?> devApp = createAndStartContainer();
        try {
            int devPort = devApp.getMappedPort(8085);
            String baseUrl = "http://localhost:" + devPort;

            // 1. Создаем первую карту
            Card firstCard = new Card("5555666677778888", 10000L, "12/25", 123);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Card> firstRequest = new HttpEntity<>(firstCard, headers);

            ResponseEntity<Void> firstResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", firstRequest, Void.class);
            assertEquals(200, firstResponse.getStatusCodeValue());

            // 2. Пытаемся создать вторую карту с тем же номером
            Card secondCard = new Card("5555666677778888", 20000L, "01/26", 456);
            HttpEntity<Card> secondRequest = new HttpEntity<>(secondCard, headers);

            ResponseEntity<Void> secondResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", secondRequest, Void.class);

            // 3. Ожидаем получить статус 500 (Internal Server Error)
            assertEquals(400, secondResponse.getStatusCodeValue());
        } finally {
            devApp.stop();
        }
    }

    @Test
    void cardDoesNotChangeWhenCreatingSameNumber() {
        GenericContainer<?> devApp = createAndStartContainer();
        try {
            int devPort = devApp.getMappedPort(8085);
            String baseUrl = "http://localhost:" + devPort;

            // 1. Создаем первую карту с определенными параметрами
            Card originalCard = new Card("9876543210987654", 15000L, "06/27", 789);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Card> originalRequest = new HttpEntity<>(originalCard, headers);

            ResponseEntity<Void> originalResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", originalRequest, Void.class);
            assertEquals(200, originalResponse.getStatusCodeValue());

            // 2. Получаем список карт и проверяем исходную карту
            ResponseEntity<Card[]> initialCardsResponse = restTemplate.getForEntity(
                baseUrl + "/cards", Card[].class);
            assertEquals(200, initialCardsResponse.getStatusCodeValue());
            assertNotNull(initialCardsResponse.getBody());
            assertEquals(1, initialCardsResponse.getBody().length);

            Card retrievedOriginalCard = initialCardsResponse.getBody()[0];
            assertEquals("9876543210987654", retrievedOriginalCard.getNumber());
            assertEquals(15000L, retrievedOriginalCard.getBalance());
            assertEquals(789, retrievedOriginalCard.getCvv());

            // 3. Пытаемся создать карту с тем же номером, но другими параметрами
            Card duplicateCard = new Card("9876543210987654", 50000L, "12/28", 111);
            HttpEntity<Card> duplicateRequest = new HttpEntity<>(duplicateCard, headers);

            ResponseEntity<Void> duplicateResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", duplicateRequest, Void.class);
            assertEquals(400, duplicateResponse.getStatusCodeValue());

            // 4. Проверяем, что исходная карта не изменилась
            ResponseEntity<Card[]> finalCardsResponse = restTemplate.getForEntity(
                baseUrl + "/cards", Card[].class);
            assertEquals(200, finalCardsResponse.getStatusCodeValue());
            assertNotNull(finalCardsResponse.getBody());
            assertEquals(1, finalCardsResponse.getBody().length);

            Card unchangedCard = finalCardsResponse.getBody()[0];
            assertEquals("9876543210987654", unchangedCard.getNumber());
            assertEquals(15000L, unchangedCard.getBalance()); // Баланс не изменился
            assertEquals(789, unchangedCard.getCvv()); // CVV не изменился
        } finally {
            devApp.stop();
        }
    }
}