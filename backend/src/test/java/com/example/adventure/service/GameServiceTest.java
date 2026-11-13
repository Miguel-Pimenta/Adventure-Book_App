package com.example.adventure.service;

import com.example.adventure.model.AdventureModels.Game;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new GameService(new BookService(Jackson2ObjectMapperBuilder.json().build()));
    }

    @Test
    void startsAtBeginningWithTenHealth() {
        Game game = service.startGame("the-forest-gate");
        assertEquals("the-forest-gate", game.bookId());
        assertEquals("The Forest Gate", game.bookTitle());
        assertEquals("1", game.currentSection().id());
        assertEquals(10, game.health());
        assertFalse(game.ended());
    }

    @Test
    void navigatesThroughAnIntermediateSectionToEnding() {
        Game next = service.chooseOption("the-forest-gate", "1", 0, 10, false);
        assertEquals("2", next.currentSection().id());
        assertFalse(next.ended());
        assertEquals(6, next.health());
        assertEquals("You scrape your hands.", next.consequenceText());
        Game end = service.chooseOption("the-forest-gate", "2", 0, next.health(), next.ended());
        assertEquals("3", end.currentSection().id());
        assertTrue(end.ended());
        assertEquals(9, end.health());
        assertEquals("Rest restores your strength.", end.consequenceText());
    }

    @Test
    void fatalConsequencePreventsMovingToEnding() {
        Game game = service.chooseOption("the-forest-gate", "2", 1, 6, false);
        assertEquals(0, game.health());
        assertTrue(game.ended());
        assertEquals("2", game.currentSection().id());
        assertEquals("The thorns cut deeply.", game.consequenceText());
    }

}
