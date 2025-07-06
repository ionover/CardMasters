package com.example.cardmasters.cotrollers;

import com.example.cardmasters.dto.ConfirmRequest;
import com.example.cardmasters.dto.TransferRequest;
import com.example.cardmasters.handlers.ConfirmHandler;
import com.example.cardmasters.handlers.TransferHandler;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MoneyController {

    private final TransferHandler transferHandler;
    private final ConfirmHandler confirmHandler;

    public MoneyController(TransferHandler transferHandler, ConfirmHandler confirmHandler) {
        this.transferHandler = transferHandler;
        this.confirmHandler = confirmHandler;
    }

    @PostMapping("/transfer")
    public Integer transfer(@Valid @RequestBody TransferRequest transferRequest) {
        return transferHandler.handle(transferRequest);
    }

    @PostMapping("/confirmOperation")
    public Integer confirmOperation(@Valid @RequestBody ConfirmRequest confirmRequest) {
        return confirmHandler.handle(confirmRequest);
    }
}
