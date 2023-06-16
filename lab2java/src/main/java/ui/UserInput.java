package ui;

import java.util.Set;

public class UserInput {

    public final Set<String> clause;

    public final char command;

    public UserInput(Set<String> clause, char command) {
        this.clause = clause;
        this.command = command;
    }
}
