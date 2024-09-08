package dev.lukas.todocli.todo.service;

import dev.lukas.todocli.todo.model.Todo;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TodoService {

    private static final String CSV_FILE_NAME = ".todo/data/todos.csv";
    private static final String SYSTEM_PROPERTY_HOME = "user.home";

    private final File csvDataStore;

    public TodoService() {
        this.csvDataStore = initDataFile();
    }

    private File initDataFile() {
        File csvDataFile = resolveDataStorePath().toFile();
        if (!csvDataFile.exists()) {
            return createDataStoreFile(csvDataFile);
        }
        return csvDataFile;
    }

    private Path resolveDataStorePath() {
        String homeDirectory = System.getProperty(SYSTEM_PROPERTY_HOME);
        if (Objects.isNull(homeDirectory)) {
            throw new IllegalStateException("System property '" + SYSTEM_PROPERTY_HOME + "' is not set");
        }
        return Path.of(homeDirectory, CSV_FILE_NAME).toAbsolutePath().normalize();
    }

    private File createDataStoreFile(File csvDataFile) {
        File parentFile = csvDataFile.getParentFile();
        if (Objects.isNull(parentFile)) {
            throw new IllegalStateException("Could not get parent directory from data store file");
        }
        if (!parentFile.mkdirs()) {
            throw new IllegalStateException("Could not create directory to data store file");
        }
        try {
            if (!csvDataFile.createNewFile()) {
                throw new IllegalStateException("Could not create file to data store file");
            }
            return csvDataFile;
        } catch (IOException e) {
            throw new IllegalStateException("Exception while creating data store file", e);
        }
    }

    private void writeData(Todo data, File csvDataFile) {
        assert csvDataStore != null && csvDataStore.exists() : "Csv data store does not exist";
        assert data != null : "Data must not be null";
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(csvDataFile, true))) {
            writer.println(data.toCsvDataRow());
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Could not write to data store. File not found.", e);
        }
    }

    public void add(String newTodoContent) {
        Todo data = new Todo(generateNextId(), newTodoContent, false);
        writeData(data, this.csvDataStore);
    }

    private List<Todo> readData(File csvDataStore) {
        assert csvDataStore != null && csvDataStore.exists() : "Csv data store does not exist";

        try (BufferedReader reader = Files.newBufferedReader(csvDataStore.toPath())) {
            return reader.lines()
                    .filter(Objects::nonNull)
                    .filter(line -> !line.isBlank())
                    .map(line -> line.split(","))
                    .map(Todo::fromCsvRow).toList();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read from data store.", e);
        }
    }

    public List<Todo> showAll() {
        return readData(this.csvDataStore);
    }

    public Optional<Todo> show(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("Id must not be null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("Id must not be negative");
        }
        List<Todo> data = this.readData(this.csvDataStore);
        return data.stream()
                .filter(todo -> todo.id() == id)
                .findFirst();
    }

    private long generateNextId() {
        return this.readData(this.csvDataStore).size();
    }
}
