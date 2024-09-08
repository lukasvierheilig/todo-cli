package dev.lukas.todocli.todo.commands;

import dev.lukas.todocli.todo.model.Todo;
import dev.lukas.todocli.todo.service.TodoService;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.util.List;

@Command(group = "Todo Commands", description = "Command managing todos.")
public class TodoCommands {
}
