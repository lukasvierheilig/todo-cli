package dev.lukas.todocli.todo.model;

public record Todo(long id, String contend, boolean completed) {

    public Todo{
        if (contend == null || contend.isBlank()) {
            throw new IllegalArgumentException("Contend cannot be null or blank");
        }
    }

    public String toCsvDataRow() {
        return String.format("%s,%s,%s", id, contend, completed);
    }

    public static Todo fromCsvRow(String[] row) {
        if (row.length != 3) {
            throw new IllegalArgumentException("Todo csv row must have exactly 3 entries");
        }
        return new Todo(Long.parseLong(row[0]), row[1], Boolean.parseBoolean(row[2]));
    }
}
