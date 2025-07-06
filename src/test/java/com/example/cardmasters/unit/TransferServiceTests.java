package com.example.cardmasters.unit;

import com.example.cardmasters.dto.Amount;
import com.example.cardmasters.dto.Card;
import com.example.cardmasters.dto.TransferRequest;
import com.example.cardmasters.repository.CardRepos;
import com.example.cardmasters.services.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransferServiceTests {

    @Mock
    private CardRepos cardRepos;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transferService = new TransferService(cardRepos);
    }

    @Test
    void doTransfer_SuccessfulTransfer_PositiveScenario() {
        Card fromCard = new Card("1111222233334444", 10000L, "12/25", 123);
        Card toCard = new Card("5555666677778888", 5000L, "06/26", 456);
        
        TransferRequest transferRequest = new TransferRequest(
            "1111222233334444", 
            "12/25", 
            "123", 
            "5555666677778888", 
            new Amount(2000, "RUB")
        );

        when(cardRepos.getCard("1111222233334444")).thenReturn(Optional.of(fromCard));
        when(cardRepos.getCard("5555666677778888")).thenReturn(Optional.of(toCard));

        transferService.doTransfer(transferRequest);

        assertEquals(8000L, fromCard.getBalance()); // 10000 - 2000
        assertEquals(7000L, toCard.getBalance());   // 5000 + 2000
        
        verify(cardRepos, times(1)).getCard("1111222233334444");
        verify(cardRepos, times(1)).getCard("5555666677778888");
    }

    @Test
    void doTransfer_InsufficientFunds_NegativeScenario() {
        Card fromCard = new Card("1111222233334444", 1000L, "12/25", 123);
        Card toCard = new Card("5555666677778888", 5000L, "06/26", 456);
        
        TransferRequest transferRequest = new TransferRequest(
            "1111222233334444", 
            "12/25", 
            "123", 
            "5555666677778888", 
            new Amount(2000, "RUB") // Пытаемся перевести больше, чем есть на карте
        );

        when(cardRepos.getCard("1111222233334444")).thenReturn(Optional.of(fromCard));
        when(cardRepos.getCard("5555666677778888")).thenReturn(Optional.of(toCard));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> transferService.doTransfer(transferRequest)
        );
        
        assertEquals("Недостаточно средств на карте отправителя", exception.getMessage());
        
        assertEquals(1000L, fromCard.getBalance());
        assertEquals(5000L, toCard.getBalance());
        
        verify(cardRepos, times(1)).getCard("1111222233334444");
        verify(cardRepos, times(1)).getCard("5555666677778888");
    }
}
