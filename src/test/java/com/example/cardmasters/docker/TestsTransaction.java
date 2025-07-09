package com.example.cardmasters.docker;

import com.example.cardmasters.dto.Amount;
import com.example.cardmasters.dto.Card;
import com.example.cardmasters.dto.ConfirmRequest;
import com.example.cardmasters.dto.TransferRequest;
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
class TestsTransaction {

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
    void canCreateMoneyTransfer() {
        GenericContainer<?> devApp = createAndStartContainer();
        try {
            int devPort = devApp.getMappedPort(8085);
            String baseUrl = "http://localhost:" + devPort;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 1. Создаем первую карту (отправитель)
            Card senderCard = new Card("1111222233334444", 50000L, "12/25", 123);
            HttpEntity<Card> senderRequest = new HttpEntity<>(senderCard, headers);
            ResponseEntity<Void> senderResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", senderRequest, Void.class);
            assertEquals(200, senderResponse.getStatusCodeValue());

            // 2. Создаем вторую карту (получатель)
            Card receiverCard = new Card("5555666677778888", 10000L, "06/27", 456);
            HttpEntity<Card> receiverRequest = new HttpEntity<>(receiverCard, headers);
            ResponseEntity<Void> receiverResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", receiverRequest, Void.class);
            assertEquals(200, receiverResponse.getStatusCodeValue());

            // 3. Выполняем перевод
            Amount transferAmount = new Amount(15000, "RUB");
            TransferRequest transferRequest = new TransferRequest(
                "1111222233334444", "12/25", "123", "5555666677778888", transferAmount);

            HttpEntity<TransferRequest> transferHttpRequest = new HttpEntity<>(transferRequest, headers);
            ResponseEntity<Integer> transferResponse = restTemplate.postForEntity(
                baseUrl + "/transfer", transferHttpRequest, Integer.class);
            assertEquals(200, transferResponse.getStatusCodeValue());
            assertNotNull(transferResponse.getBody());

            // 4. Проверяем, что балансы НЕ изменились (нет подтверждения)
            ResponseEntity<Card[]> cardsResponse = restTemplate.getForEntity(
                baseUrl + "/cards", Card[].class);
            assertEquals(200, cardsResponse.getStatusCodeValue());
            Card[] cards = cardsResponse.getBody();

            for (Card card : cards) {
                if ("1111222233334444".equals(card.getNumber())) {
                    assertEquals(50000L, card.getBalance()); // Баланс отправителя НЕ изменился
                } else if ("5555666677778888".equals(card.getNumber())) {
                    assertEquals(10000L, card.getBalance()); // Баланс получателя НЕ изменился
                }
            }

        } finally {
            devApp.stop();
        }
    }

    @Test
    void successfulPayment() {
        GenericContainer<?> devApp = createAndStartContainer();
        try {
            int devPort = devApp.getMappedPort(8085);
            String baseUrl = "http://localhost:" + devPort;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 1. Создаем первую карту (отправитель)
            Card senderCard = new Card("1111222233334444", 50000L, "12/25", 123);
            HttpEntity<Card> senderRequest = new HttpEntity<>(senderCard, headers);
            ResponseEntity<Void> senderResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", senderRequest, Void.class);
            assertEquals(200, senderResponse.getStatusCodeValue());

            // 2. Создаем вторую карту (получатель)
            Card receiverCard = new Card("5555666677778888", 10000L, "06/27", 456);
            HttpEntity<Card> receiverRequest = new HttpEntity<>(receiverCard, headers);
            ResponseEntity<Void> receiverResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", receiverRequest, Void.class);
            assertEquals(200, receiverResponse.getStatusCodeValue());

            // 3. Выполняем перевод
            Amount transferAmount = new Amount(15000, "RUB");
            TransferRequest transferRequest = new TransferRequest(
                "1111222233334444", "12/25", "123", "5555666677778888", transferAmount);

            HttpEntity<TransferRequest> transferHttpRequest = new HttpEntity<>(transferRequest, headers);
            ResponseEntity<Integer> transferResponse = restTemplate.postForEntity(
                baseUrl + "/transfer", transferHttpRequest, Integer.class);
            assertEquals(200, transferResponse.getStatusCodeValue());
            assertNotNull(transferResponse.getBody());

            Integer operationId = transferResponse.getBody();

            // 4. Подтверждаем операцию
            ConfirmRequest confirmRequest = new ConfirmRequest(operationId, 777);
            HttpEntity<ConfirmRequest> confirmHttpRequest = new HttpEntity<>(confirmRequest, headers);
            ResponseEntity<Integer> confirmResponse = restTemplate.postForEntity(
                baseUrl + "/confirmOperation", confirmHttpRequest, Integer.class);
            assertEquals(200, confirmResponse.getStatusCodeValue());

            // 5. Проверяем, что балансы изменились после подтверждения
            ResponseEntity<Card[]> cardsResponse = restTemplate.getForEntity(
                baseUrl + "/cards", Card[].class);
            assertEquals(200, cardsResponse.getStatusCodeValue());
            Card[] cards = cardsResponse.getBody();

            for (Card card : cards) {
                if ("1111222233334444".equals(card.getNumber())) {
                    assertEquals(35000L, card.getBalance()); // 50000 - 15000 = 35000
                } else if ("5555666677778888".equals(card.getNumber())) {
                    assertEquals(25000L, card.getBalance()); // 10000 + 15000 = 25000
                }
            }

        } finally {
            devApp.stop();
        }
    }

    @Test
    void transferInsufficientFunds() {
        GenericContainer<?> devApp = createAndStartContainer();
        try {
            int devPort = devApp.getMappedPort(8085);
            String baseUrl = "http://localhost:" + devPort;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 1. Создаем первую карту (отправитель) с небольшим балансом
            Card senderCard = new Card("1111222233334444", 5000L, "12/25", 123);
            HttpEntity<Card> senderRequest = new HttpEntity<>(senderCard, headers);
            ResponseEntity<Void> senderResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", senderRequest, Void.class);
            assertEquals(200, senderResponse.getStatusCodeValue());

            // 2. Создаем вторую карту (получатель)
            Card receiverCard = new Card("5555666677778888", 10000L, "06/27", 456);
            HttpEntity<Card> receiverRequest = new HttpEntity<>(receiverCard, headers);
            ResponseEntity<Void> receiverResponse = restTemplate.postForEntity(
                baseUrl + "/addCard", receiverRequest, Void.class);
            assertEquals(200, receiverResponse.getStatusCodeValue());

            // 3. Пытаемся перевести больше, чем есть на карте (15000 > 5000)
            Amount transferAmount = new Amount(15000, "RUB");
            TransferRequest transferRequest = new TransferRequest(
                "1111222233334444", "12/25", "123", "5555666677778888", transferAmount);

            HttpEntity<TransferRequest> transferHttpRequest = new HttpEntity<>(transferRequest, headers);
            ResponseEntity<Integer> transferResponse = restTemplate.postForEntity(
                baseUrl + "/transfer", transferHttpRequest, Integer.class);
            assertEquals(200, transferResponse.getStatusCodeValue());
            assertNotNull(transferResponse.getBody());

            Integer operationId = transferResponse.getBody();

            // 4. Подтверждаем операцию
            ConfirmRequest confirmRequest = new ConfirmRequest(operationId, 777);
            HttpEntity<ConfirmRequest> confirmHttpRequest = new HttpEntity<>(confirmRequest, headers);
            ResponseEntity<Integer> confirmResponse = restTemplate.postForEntity(
                baseUrl + "/confirmOperation", confirmHttpRequest, Integer.class);
            assertEquals(200, confirmResponse.getStatusCodeValue());

            // 5. Проверяем, что балансы НЕ изменились (недостаточно средств)
            ResponseEntity<Card[]> cardsResponse = restTemplate.getForEntity(
                baseUrl + "/cards", Card[].class);
            assertEquals(200, cardsResponse.getStatusCodeValue());
            Card[] cards = cardsResponse.getBody();

            for (Card card : cards) {
                if ("1111222233334444".equals(card.getNumber())) {
                    assertEquals(5000L, card.getBalance()); // Баланс остался 5000
                } else if ("5555666677778888".equals(card.getNumber())) {
                    assertEquals(10000L, card.getBalance()); // Баланс остался 10000
                }
            }

        } finally {
            devApp.stop();
        }
    }
}
