package com.example.cardmasters.handlers;

import com.example.cardmasters.dto.ConfirmRequest;
import com.example.cardmasters.dto.TransferRequest;
import com.example.cardmasters.repository.TransactionsRepos;
import com.example.cardmasters.services.TransferService;

import java.util.Optional;

public class ConfirmHandler {

    private final TransferService transferService;
    private final TransactionsRepos transactionsRepos;

    public ConfirmHandler(TransferService transferService, TransactionsRepos transactionsRepos) {
        this.transferService = transferService;
        this.transactionsRepos = transactionsRepos;
    }


    public Integer handle(ConfirmRequest confirmRequest) {
        Integer currentTransactionId = confirmRequest.getOperationId();

        transferService.doTransfer(transactionsRepos.get(currentTransactionId));

        return currentTransactionId;
    }
}
