package com.example.adventure.service;

import com.example.adventure.model.AdventureModels.Book;
import com.example.adventure.model.AdventureModels.Consequence;
import com.example.adventure.model.AdventureModels.Game;
import com.example.adventure.model.AdventureModels.Option;
import com.example.adventure.model.AdventureModels.Section;
import com.example.adventure.model.AdventureModels.SectionType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GameService {
    private final BookService bookService;

    public GameService(BookService bookService) {
        this.bookService = bookService;
    }

    public Game startGame(String bookId) {
        Book book = getPlayableBook(bookId);
        for (Section section : book.sections()) {
            if (section.type() == SectionType.BEGIN) {
                return createGame(book, section, Game.STARTING_HEALTH, null);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book has no beginning.");
    }

    public Game chooseOption(String bookId, String sectionId, int optionIndex, int health, boolean ended) {
        Book book = getPlayableBook(bookId);
        Section current = bookService.findSection(book, sectionId);
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found.");
        }
        if (ended || health <= 0 || current.type() == SectionType.END) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The adventure has already ended.");
        }
        if (optionIndex < 0 || optionIndex >= current.options().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choice not found.");
        }
        Option option = current.options().get(optionIndex);
        Section next = bookService.findSection(book, option.gotoId());
        if (next == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination not found.");
        }
        Consequence consequence = option.consequence();
        health = applyConsequence(health, consequence);

        String message = consequence == null ? null : consequence.text();
        if (health <= 0) {
            return createGame(book, current, 0, message);
        }
        return createGame(book, next, health, message);
    }

    private Book getPlayableBook(String bookId) {
        Book book = bookService.findBook(bookId);
        if (book == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found.");
        }
        if (!book.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This book is invalid.");
        }
        return book;
    }

    private int applyConsequence(int health, Consequence consequence) {
        if (consequence == null || consequence.type() == null) {
            return health;
        }
        switch (consequence.type()) {
            case LOSE_HEALTH:
                return health - consequence.value();
            case GAIN_HEALTH:
                return health + consequence.value();
            default:
                return health;
        }
    }

    private Game createGame(Book book, Section section, int health, String consequenceText) {
        boolean ended = health <= 0 || section.type() == SectionType.END;
        return new Game(book.id(), book.title(), section, health, ended, consequenceText);
    }
}
