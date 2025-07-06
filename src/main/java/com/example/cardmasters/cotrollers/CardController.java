package com.example.cardmasters.cotrollers;

import com.example.cardmasters.dto.Card;
import com.example.cardmasters.handlers.AddCardHandler;
import com.example.cardmasters.repository.CardRepos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class CardController {

    private final AddCardHandler addCardHandler;
    private final CardRepos cardRepos;

    public CardController(AddCardHandler addCardHandler, CardRepos cardRepos) {
        this.addCardHandler = addCardHandler;
        this.cardRepos = cardRepos;
    }

    @PostMapping("/addCard")
    public void addCard(@Valid @RequestBody Card card) {
        addCardHandler.handle(card);
    }

    @GetMapping("/cards")
    public Collection<Card> getAllCards() {
        return cardRepos.getAllCards();
    }
}
