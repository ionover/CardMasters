package com.example.cardmasters.repository;

import com.example.cardmasters.dto.TransferRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransactionsRepos {

    private final ConcurrentHashMap<Integer, TransferRequest> transactions = new ConcurrentHashMap<>();

    public void save(Integer id, TransferRequest request) {
        transactions.put(id, request);
    }

    public Optional<TransferRequest> get(Integer id) {
        return Optional.ofNullable(transactions.get(id));
    }
}
