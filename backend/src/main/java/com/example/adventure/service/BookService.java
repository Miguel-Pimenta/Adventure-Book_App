package com.example.adventure.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.adventure.model.AdventureModels.Book;
import com.example.adventure.model.AdventureModels.Section;
import com.example.adventure.model.AdventureModels.Option;
import com.example.adventure.model.AdventureModels.SectionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final List<Book> books = new ArrayList<>();

    private final ObjectMapper objectMapper;

    public BookService(ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        loadBooks("classpath:books/*.json");
    }

    private void loadBooks(String pattern) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (Resource resource : resolver.getResources(pattern)) {
            try (InputStream input = resource.getInputStream()) {
                Book book = objectMapper.readValue(input, Book.class);
                String filename = resource.getFilename();
                books.add(prepareBook(book, filename.substring(0, filename.length() - 5)));
            }
        }
    }

    private Book prepareBook(Book book, String id) {
        return new Book(id, book.title(), book.author(), book.difficulty(),
                book.sections(), validateBook(book));
    }

    public List<String> validateBook(Book book) {
        List<String> errors = new ArrayList<>();
        int beginnings = 0;
        int endings = 0;
        for (Section section : book.sections()) {
            if (section.type() == SectionType.BEGIN) {
                beginnings++;
            }
            if (section.type() == SectionType.END) {
                endings++;
            } else if (section.options().isEmpty()) {
                errors.add("Section " + section.id() + " is not an ending and has no options.");
            }
            for (Option option : section.options()) {
                if (findSection(book, option.gotoId()) == null) {
                    errors.add("Section " + section.id() + " references missing section " + option.gotoId() + ".");
                }
            }
        }
        if (beginnings != 1) {
            errors.add("A book must have exactly one beginning; found " + beginnings + ".");
        }
        if (endings == 0) {
            errors.add("A book must have at least one ending.");
        }
        return errors;
    }

    public Section findSection(Book book, String sectionId) {
        for (Section section : book.sections()) {
            if (section.id().equals(sectionId)) {
                return section;
            }
        }
        return null;
    }

    public Book findBook(String id) {
        for (Book book : books) {
            if (book.id().equals(id)) {
                return book;
            }
        }
        return null;
    }

    public List<Book> getBooks() {
        return new ArrayList<>(books);
    }
}
