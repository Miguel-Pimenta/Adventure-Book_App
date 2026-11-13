package com.example.adventure.service;

import com.example.adventure.model.AdventureModels.Book;
import com.example.adventure.model.AdventureModels.Difficulty;
import com.example.adventure.model.AdventureModels.Section;
import com.example.adventure.model.AdventureModels.SectionType;
import com.example.adventure.model.AdventureModels.Option;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    private BookService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new BookService(
            Jackson2ObjectMapperBuilder.json().build()
        );
    }

    private Section beginning() {
        return new Section("1", "Start", SectionType.BEGIN,
                List.of(new Option("Continue", "2", null)));
    }

    private Section ending() {
        return new Section("2", "Finish", SectionType.END, List.of());
    }

    private Book book(List<Section> sections) {
        return new Book(null, "Test", "Test author", Difficulty.EASY, sections, List.of());
    }

    @Test
    void loadsAllBooksAndNormalizesMixedIds() throws Exception {
        assertEquals(5, service.getBooks().size());
        Book prisoner = service.findBook("the-prisoner");
        assertNotNull(prisoner);
        assertEquals("1", prisoner.sections().get(0).id());
        assertEquals("500", prisoner.sections().get(0).options().get(0).gotoId());
        assertNotNull(service.findSection(prisoner, "500"));
        assertEquals(6, prisoner.sections().get(1).options().get(0).consequence().value());
    }

    @Test
    void acceptsValidBook() throws Exception {
        assertTrue(service.validateBook(book(List.of(beginning(), ending()))).isEmpty());
    }

    @Test
    void rejectsMissingBeginning() throws Exception {
        Section node = new Section("1", "Start", SectionType.NODE, beginning().options());
        List<String> errors = service.validateBook(book(List.of(node, ending())));
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("exactly one beginning"));
    }

    @Test
    void rejectsMultipleBeginnings() throws Exception {
        Section second = new Section("3", "Another start", SectionType.BEGIN, beginning().options());
        List<String> errors = service.validateBook(book(List.of(beginning(), ending(), second)));
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("found 2"));
    }

    @Test
    void rejectsMissingEnding() throws Exception {
        Section begin = new Section("1", "Start", SectionType.BEGIN,
                List.of(new Option("Repeat", "1", null)));
        List<String> errors = service.validateBook(book(List.of(begin)));
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("at least one ending"));
    }

    @Test
    void rejectsMissingDestination() throws Exception {
        Section begin = new Section("1", "Start", SectionType.BEGIN,
                List.of(new Option("Missing", "999", null)));
        List<String> errors = service.validateBook(book(List.of(begin, ending())));
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("missing section 999"));
    }

    @Test
    void rejectsNonEndingWithoutOptions() throws Exception {
        Section begin = new Section("1", "Start", SectionType.BEGIN, List.of());
        List<String> errors = service.validateBook(book(List.of(begin, ending())));
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("has no options"));
    }

}
