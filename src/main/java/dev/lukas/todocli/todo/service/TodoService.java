package dev.lukas.todocli.todo.service;

import dev.lukas.todocli.todo.model.Todo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TodoService {

    private final List<Todo> db = new ArrayList<>();

    public void add(String newTodoContent) {
        this.db.add(new Todo(generateNextId(), newTodoContent, false));
    }

    public List<Todo> show(Long id) {
        if (Objects.nonNull(id)) {
            return db.stream().filter(todo -> todo.id() == id).toList();
        }
        return db;
    }

    private long generateNextId() {
        return db.size();
    }
}
