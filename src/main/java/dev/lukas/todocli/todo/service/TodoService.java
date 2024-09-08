package dev.lukas.todocli.todo.service;

import dev.lukas.todocli.todo.model.Todo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TodoService {

    private static final String CSV_FILE_NAME = ".todo/data/todos.csv";
    private static final String SYSTEM_PROPERTY_HOME = "user.home";
    private static final String HEADER_ID = "id";
    private static final String HEADER_CONTENT = "content";
    private static final String HEADER_COMPLETED = "completed";
    private static final String[] HEADERS = {HEADER_ID, HEADER_CONTENT, HEADER_COMPLETED};

    private final Path csvDataStorePath;

    public TodoService() {
        this.csvDataStorePath = resolveDataStorePath();
    }

    public List<Todo> showAll() {
        return readData();
    }

    public Optional<Todo> show(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("Id must not be null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("Id must not be negative");
        }
        List<Todo> data = this.readData();
        return data.stream()
                .filter(todo -> todo.id() == id)
                .findFirst();
    }

    public void add(String newTodoContent) {
        if (newTodoContent.isBlank()) {
            throw new IllegalArgumentException("Content must not be blank");
        }
        Todo data = new Todo(generateNextId(), newTodoContent, false);
        writeData(data);
    }

    private Path resolveDataStorePath() {
        String homeDirectory = System.getProperty(SYSTEM_PROPERTY_HOME);
        if (Objects.isNull(homeDirectory) || homeDirectory.isBlank()) {
            throw new IllegalStateException("System property '" + SYSTEM_PROPERTY_HOME + "' is not set");
        }
        return Path.of(homeDirectory, CSV_FILE_NAME).toAbsolutePath().normalize();
    }

    private void writeData(Todo data) {
        assert data != null : "Data must not be null";
        assert csvDataStorePath != null : "Csv data path must not be null";

        CSVFormat csvFormat;
        StandardOpenOption[] options;

        if (!csvDataStorePath.toFile().exists()) {
            options = new StandardOpenOption[]{StandardOpenOption.CREATE_NEW};
            csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(HEADERS)
                    .build();

        } else {
            options = new StandardOpenOption[]{StandardOpenOption.APPEND};
            csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(HEADERS)
                    .setSkipHeaderRecord(true)
                    .build();
        }

        try (CSVPrinter csvPrinter = new CSVPrinter(
                Files.newBufferedWriter(
                        csvDataStorePath,
                        options),
                csvFormat)) {
            csvPrinter.printRecord(data.id(), data.contend(), data.completed());
            csvPrinter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read from data store.", e);
        }
    }

    private List<Todo> readData() {
        if (csvDataStorePath == null || !csvDataStorePath.toFile().exists()) {
            return Collections.emptyList();
        }

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();

        try (CSVParser csvParser = csvFormat.parse(Files.newBufferedReader(csvDataStorePath))) {
            return csvParser.getRecords().stream()
                    .map(csvRecordRow -> new String[]{
                            csvRecordRow.get(HEADER_ID),
                            csvRecordRow.get(HEADER_CONTENT),
                            csvRecordRow.get(HEADER_COMPLETED)})
                    .map(Todo::fromCsvRow)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read from data store.", e);
        }
    }

    private long generateNextId() {
        return this.readData().size();
    }
}
