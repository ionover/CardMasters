package com.example.cardmasters.docker;

import com.example.cardmasters.dto.Card;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseDockerTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected GenericContainer<?> devApp;
    protected String baseUrl;
    protected HttpHeaders headers;

    @BeforeEach
    void setUp() {
        devApp = createAndStartContainer();
        int devPort = devApp.getMappedPort(5500);
        baseUrl = "http://localhost:" + devPort;

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @AfterEach
    void tearDown() {
        if (devApp != null) {
            devApp.stop();
        }
    }

    private GenericContainer<?> createAndStartContainer() {
        GenericContainer<?> container = new GenericContainer<>("devapp:latest")
                .withExposedPorts(5500)
                .waitingFor(Wait.forHttp("/cards").forStatusCode(200));
        container.start();

        return container;
    }

    protected ResponseEntity<Card[]> getAllCards() {
        return restTemplate.getForEntity(baseUrl + "/cards", Card[].class);
    }

    protected ResponseEntity<Void> createCard(Card card) {
        HttpEntity<Card> request = new HttpEntity<>(card, headers);

        return restTemplate.postForEntity(baseUrl + "/addCard", request, Void.class);
    }

    protected void assertCardCreatedSuccessfully(Card card) {
        ResponseEntity<Void> response = createCard(card);
        assertEquals(200, response.getStatusCodeValue());
    }

    protected void assertCardCreationFailed(Card card, int expectedStatusCode) {
        ResponseEntity<Void> response = createCard(card);
        assertEquals(expectedStatusCode, response.getStatusCodeValue());
    }

    protected Card findCardByNumber(Card[] cards, String cardNumber) {
        for (Card card: cards) {
            if (cardNumber.equals(card.getNumber())) {
                return card;
            }
        }

        return null;
    }

    protected void assertCardBalance(String cardNumber, long expectedBalance) {
        ResponseEntity<Card[]> cardsResponse = getAllCards();
        assertEquals(200, cardsResponse.getStatusCodeValue());
        Card[] cards = cardsResponse.getBody();

        Card card = findCardByNumber(cards, cardNumber);
        assertEquals(expectedBalance, card.getBalance());
    }

    protected void assertCardsCount(int expectedCount) {
        ResponseEntity<Card[]> response = getAllCards();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedCount, response.getBody().length);
    }
}
