package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    private static final DataLoader DATA_LOADER = new DataLoader("files/");

    public static final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public static void main(String[] args) {
        final var arg = args[0];

        if (arg.equals("resolution")) {
            final var clauses = DATA_LOADER.loadClauses(args[1]);
            plResolution(clauses);
        }
        if (arg.equals("cooking")) {
            final var clauses = DATA_LOADER.loadClauses(args[1]);
            final var userInputs = DATA_LOADER.loadUserInputs(args[2]);
            resolveUserInputs(clauses, userInputs);
        }
    }

    public static void resolveUserInputs(List<Set<String>> knowledgeBase, List<UserInput> userInputs) {
        for (final var userInput : userInputs) {
            out.printf("User's command: %s %c%n", clauseToString(userInput.clause), userInput.command);

            if (userInput.command == '+') {
                knowledgeBase.add(userInput.clause);
                out.printf("Added %s%n", clauseToString(userInput.clause));
            }
            if (userInput.command == '-') {
                knowledgeBase.remove(userInput.clause);
                out.printf("Removed %s%n", clauseToString(userInput.clause));
            }
            if (userInput.command == '?') {
                knowledgeBase.add(userInput.clause);
                plResolution(knowledgeBase);
            }

            out.println();
        }
    }

    public static void plResolution(List<Set<String>> entryClauses) {
        final var lastClause = entryClauses.get(entryClauses.size() - 1);
        final var invertedLastClause = invertClause(lastClause);
        entryClauses.remove(lastClause);

        final var clauses = new LinkedHashSet<Set<String>>(new HashSet<>());
        clauses.addAll(entryClauses);
        clauses.addAll(invertedLastClause);
        removeUnnecessary(clauses);

        int i = 0;
        for (final var clause : clauses) {
            out.printf("%d. %s%n", ++i, clauseToString(clause));
        }
        out.println("===============");

        final var new_ = new LinkedHashSet<Set<String>>(new HashSet<>());
        final var all = new ArrayList<>(new HashSet<>());
        all.addAll(clauses);

        while (true) {
            for (final var t : selectClauses(clauses, entryClauses)) {
                final var resolvent = plResolve(t.c1, t.c2);
                if (resolvent == null) {
                    continue;
                }

                all.add(resolvent);
                out.printf(
                    "%d. %s (%d -> %s, %d -> %s)%n",
                    ++i, clauseToString(resolvent),
                    all.lastIndexOf(t.c1) + 1, clauseToString(t.c1),
                    all.lastIndexOf(t.c2) + 1, clauseToString(t.c2)
                );

                if (resolvent.isEmpty()) {
                    out.println("===============");
                    out.printf("[CONCLUSION]: %s is true%n", clauseToString(lastClause));
                    return;
                }
                new_.add(resolvent);
            }

            if (new HashSet<>(clauses).containsAll(new_)) {
                out.println("===============");
                out.printf("[CONCLUSION]: %s is unknown%n", clauseToString(lastClause));
                return;
            }

            clauses.addAll(new_);
            removeUnnecessary(clauses);
            new_.clear();
        }
    }

    public static List<Tuple> selectClauses(LinkedHashSet<Set<String>> clauses, List<Set<String>> entryClauses) {
        final var tuples = new ArrayList<Tuple>();

        for (final var c1 : clauses) {
            if (entryClauses.contains(c1)) {
                continue;
            }

            for (final var c2 : clauses) {
                if (c1.equals(c2)) {
                    continue;
                }
                tuples.add(new Tuple(c1, c2));
            }
        }

        return tuples;
    }

    public static List<Set<String>> invertClause(Set<String> clause) {
        return clause.stream().map(Solution::negate).map(Set::of).collect(Collectors.toList());
    }

    public static void removeUnnecessary(LinkedHashSet<Set<String>> clauses) {
        final var clausesToRemove = new HashSet<Set<String>>(new HashSet<>());

        for (final var c1 : clauses) {
            if (isTautology(c1)) {
                clausesToRemove.add(c1);
                continue;
            }

            for (final var c2 : clauses) {
                if (c1.equals(c2)) {
                    continue;
                }
                if (isSubsetOf(c1, c2)) {
                    clausesToRemove.add(c2);
                }
            }
        }

        clauses.removeAll(clausesToRemove);
    }

    public static Set<String> plResolve(Set<String> C1, Set<String> C2) {
        final var resolvent = new HashSet<String>();
        final var c1 = new HashSet<>(C1);
        final var c2 = new HashSet<>(C2);

        String literalToRemove = null;
        for (final String literal : c1) {
            if (c2.contains(negate(literal))) {
                literalToRemove = nonNegated(literal);
                break;
            }
        }

        if (literalToRemove == null) {
            return null;
        }

        c1.remove(literalToRemove);
        c1.remove(negated(literalToRemove));
        c2.remove(literalToRemove);
        c2.remove(negated(literalToRemove));

        resolvent.addAll(c1);
        resolvent.addAll(c2);
        return resolvent;
    }

    public static boolean isTautology(Set<String> clause) {
        return clause.stream().anyMatch(literal -> clause.contains(negate(literal)));
    }

    public static boolean isSubsetOf(Set<String> c1, Set<String> c2) {
        return c2.containsAll(c1);
    }

    public static boolean isNegated(String literal) {
        return literal.startsWith("~");
    }

    public static String negate(String literal) {
        return isNegated(literal) ? literal.substring(1) : "~" + literal;
    }

    public static String negated(String literal) {
        return isNegated(literal) ? literal : "~" + literal;
    }

    public static String nonNegated(String literal) {
        return isNegated(literal) ? literal.substring(1) : literal;
    }

    public static String clauseToString(Set<String> clause) {
        return clause.isEmpty() ? "NIL" : String.join(" v ", clause);
    }
}
