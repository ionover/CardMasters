package com.example.cardmasters.handlers;

import com.example.cardmasters.dto.ConfirmRequest;
import com.example.cardmasters.dto.TransferRequest;
import com.example.cardmasters.exceptions.MoneyException;
import com.example.cardmasters.logs.LogWriter;
import com.example.cardmasters.repository.TransactionsRepos;
import com.example.cardmasters.services.TransferService;
import org.springframework.stereotype.Component;

@Component
public class ConfirmHandler {

    private final TransferService transferService;
    private final TransactionsRepos transactionsRepos;
    private final LogWriter logWriter;

    public ConfirmHandler(TransferService transferService, TransactionsRepos transactionsRepos, LogWriter logWriter) {
        this.transferService = transferService;
        this.transactionsRepos = transactionsRepos;
        this.logWriter = logWriter;
    }


    public Integer handle(ConfirmRequest confirmRequest) {
        boolean success = false;
        Integer currentTransactionId = confirmRequest.getOperationId();

        TransferRequest currentTransferRequest = transactionsRepos.get(currentTransactionId);

        if (currentTransferRequest != null && confirmRequest.getCode() == 777) {
            success = transferService.doTransfer(currentTransferRequest);
        }
        logWriter.addConfirmLog(currentTransactionId, success);

        return currentTransactionId;
    }
}
