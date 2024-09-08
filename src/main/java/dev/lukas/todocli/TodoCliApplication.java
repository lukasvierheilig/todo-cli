package dev.lukas.todocli;

import dev.lukas.todocli.todo.commands.TodoCommands;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.EnableCommand;

@SpringBootApplication
@EnableCommand(TodoCommands.class)
public class TodoCliApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoCliApplication.class, args);
    }

}
