package dev.lukas.todocli.todo.commands;

import dev.lukas.todocli.todo.model.Todo;
import dev.lukas.todocli.todo.service.TodoService;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.util.List;
import java.util.Optional;

@Command(group = "Todo Commands", description = "Command managing todos.")
public class TodoCommands {

    private final TodoService todoService;

    public TodoCommands(TodoService todoService) {
        this.todoService = todoService;
    }

    @Command(description = "Add a new todo.")
    public void add(@Option(longNames = "add-todo", required = true) String todo) {
        this.todoService.add(todo);
    }

    @Command(description = "List all todos.")
    public List<Todo> list(@Option(required = false) Long id) {
        if (id == null) {
            return todoService.showAll();
        }
        return todoService.show(id).stream().toList();
    }

    @Command(description = "Set complete status for todo.")
    public void complete(@Option(required = true) Long id, @Option(defaultValue = "true") Boolean completed ) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        todoService.complete(id, Optional.ofNullable(completed).orElse(true));
    }

}
