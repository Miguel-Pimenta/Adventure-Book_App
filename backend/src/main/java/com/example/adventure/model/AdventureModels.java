package com.example.adventure.model;

import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AdventureModels {
    private AdventureModels() {
    }

    public enum Difficulty { EASY, MEDIUM, HARD }
    public enum SectionType { BEGIN, NODE, END }
    public enum ConsequenceType { LOSE_HEALTH, GAIN_HEALTH }

    public record Game(
        String bookId, 
        String bookTitle, 
        Section currentSection,
        int health, 
        boolean ended, 
        String consequenceText
    ) {
        public static final int STARTING_HEALTH = 10;
    }

    public record Book(
        String id, 
        String title, 
        String author, 
        Difficulty difficulty,           
        List<Section> sections, 
        List<String> validationErrors
    ) {
        public Book {
            if (validationErrors == null) {
                validationErrors = new ArrayList<>();
            }
        }

        @JsonProperty("valid")
        public boolean isValid() {
            return validationErrors.isEmpty();
        }
    }

    public record Section(
        String id, 
        String text, 
        SectionType type, 
        List<Option> options
    ) {
        public Section {
            if (options == null) {
                options = new ArrayList<>();
            }
        }
    }

    public record Option(
        String description, 
        String gotoId, 
        Consequence consequence
    ) {}

    public record Consequence(
        ConsequenceType type, 
        int value, 
        String text
    ) {}

    public record ChoiceRequest(
        int health, 
        boolean ended
    ) {}
}
