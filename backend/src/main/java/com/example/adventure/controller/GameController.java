package com.example.adventure.controller;

import com.example.adventure.model.AdventureModels.Game;
import com.example.adventure.model.AdventureModels.ChoiceRequest;
import com.example.adventure.service.GameService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{bookId}/start")
    public Game startGame(@PathVariable String bookId) {
        return gameService.startGame(bookId);
    }

    @PostMapping("/{bookId}/sections/{sectionId}/choices/{optionIndex}")
    public Game chooseOption(@PathVariable String bookId, @PathVariable String sectionId,
                             @PathVariable int optionIndex, @RequestBody ChoiceRequest request) {
        return gameService.chooseOption(bookId, sectionId, optionIndex, request.health(), request.ended());
    }
}
