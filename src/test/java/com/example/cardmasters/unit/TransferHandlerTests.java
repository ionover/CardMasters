package com.example.cardmasters.unit;

import com.example.cardmasters.dto.Amount;
import com.example.cardmasters.dto.TransferRequest;
import com.example.cardmasters.handlers.TransferHandler;
import com.example.cardmasters.logs.LogWriter;
import com.example.cardmasters.repository.TransactionsRepos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransferHandlerTests {

    @Mock
    private TransactionsRepos transactionsRepos;

    @Mock
    private LogWriter logWriter;

    private TransferHandler transferHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transferHandler = new TransferHandler(transactionsRepos, logWriter);
    }

    @Test
    void handle_FirstTransferRequest_ReturnsIdOne() {
        TransferRequest transferRequest = new TransferRequest(
            "1111222233334444",
            "12/25",
            "123",
            "5555666677778888",
            new Amount(1000, "RUB")
        );

        Integer result = transferHandler.handle(transferRequest);

        assertEquals(1, result);
        
        verify(transactionsRepos, times(1)).save(1, transferRequest);
//        verify(logWriter, times(1)).addTransactionLog(
//            "1111222233334444",
//            "5555666677778888",
//            transferRequest.getAmount(),
//            0,
//            "Зарегистрирован перевод с id = 1"
//        );
    }

    @Test
    void handle_MultipleTransferRequests_ReturnsIncrementingIds() {
        TransferRequest firstRequest = new TransferRequest(
            "1111222233334444",
            "12/25",
            "123",
            "5555666677778888",
            new Amount(1000, "RUB")
        );
        
        TransferRequest secondRequest = new TransferRequest(
            "9999888877776666",
            "06/26",
            "456",
            "1111222233334444",
            new Amount(2000, "RUB")
        );

        Integer firstId = transferHandler.handle(firstRequest);
        Integer secondId = transferHandler.handle(secondRequest);

        assertEquals(1, firstId);
        assertEquals(2, secondId);
        
        verify(transactionsRepos, times(1)).save(1, firstRequest);
        verify(transactionsRepos, times(1)).save(2, secondRequest);
        
//        verify(logWriter, times(1)).addTransactionLog(
//            "1111222233334444",
//            "5555666677778888",
//            firstRequest.getAmount(),
//            2,
//            "Зарегистрирован перевод с id = 1"
//        );
        
//        verify(logWriter, times(1)).addTransactionLog(
//            "9999888877776666",
//            "1111222233334444",
//            secondRequest.getAmount(),
//            2,
//            "Зарегистрирован перевод с id = 2"
//        );
    }
}
