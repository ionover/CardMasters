package com.example.cardmasters.docker;

import com.example.cardmasters.dto.ConfirmRequest;
import com.example.cardmasters.dto.TransferRequest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransactionTestHelper {

    private final TestRestTemplate restTemplate;
    private final String baseUrl;
    private final HttpHeaders headers;

    public TransactionTestHelper(TestRestTemplate restTemplate, String baseUrl, HttpHeaders headers) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.headers = headers;
    }

    public Integer executeTransfer(TransferRequest transferRequest) {
        HttpEntity<TransferRequest> request = new HttpEntity<>(transferRequest, headers);
        ResponseEntity<Integer> response = restTemplate.postForEntity(
            baseUrl + "/transfer", request, Integer.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        return response.getBody();
    }

    public ResponseEntity<Integer> confirmOperation(ConfirmRequest confirmRequest) {
        HttpEntity<ConfirmRequest> request = new HttpEntity<>(confirmRequest, headers);
        return restTemplate.postForEntity(baseUrl + "/confirmOperation", request, Integer.class);
    }

    public ResponseEntity<String> confirmOperationExpectingError(ConfirmRequest confirmRequest) {
        HttpEntity<ConfirmRequest> request = new HttpEntity<>(confirmRequest, headers);
        return restTemplate.postForEntity(baseUrl + "/confirmOperation", request, String.class);
    }

    public void setupTwoCardsForTransfer() {
        // Создаем карту отправителя
        assertCardCreatedSuccessfully(TestDataFactory.createSenderCard());
        
        // Создаем карту получателя
        assertCardCreatedSuccessfully(TestDataFactory.createReceiverCard());
    }

    public void setupTwoCardsWithLowSenderBalance() {
        // Создаем карту отправителя с низким балансом
        assertCardCreatedSuccessfully(TestDataFactory.createLowBalanceSenderCard());
        
        // Создаем карту получателя
        assertCardCreatedSuccessfully(TestDataFactory.createReceiverCard());
    }

    private void assertCardCreatedSuccessfully(com.example.cardmasters.dto.Card card) {
        HttpEntity<com.example.cardmasters.dto.Card> request = new HttpEntity<>(card, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity(baseUrl + "/addCard", request, Void.class);
        assertEquals(200, response.getStatusCodeValue());
    }
}