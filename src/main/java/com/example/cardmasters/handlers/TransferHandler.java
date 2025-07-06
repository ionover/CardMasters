package com.example.cardmasters.handlers;

import com.example.cardmasters.dto.TransferRequest;
import com.example.cardmasters.logs.LogWriter;
import com.example.cardmasters.repository.TransactionsRepos;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TransferHandler {

    private final AtomicInteger counter = new AtomicInteger(0);
    private final TransactionsRepos transactionsRepos;
    private final LogWriter logWriter;

    public TransferHandler(TransactionsRepos transactionsRepos, LogWriter logWriter) {
        this.transactionsRepos = transactionsRepos;
        this.logWriter = logWriter;
    }

    public Integer handle(TransferRequest transferRequest) {
        Integer id = counter.incrementAndGet();
        transactionsRepos.save(id, transferRequest);
        logWriter.addTransactionLog(transferRequest.getCardFromNumber(),
                transferRequest.getCardToNumber(),
                transferRequest.getAmount(),
                2,
                "Зарегистрирован перевод с id = " + id);

        return id;
    }
}
