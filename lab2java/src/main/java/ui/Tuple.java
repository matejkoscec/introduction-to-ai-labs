package ui;

import java.util.Objects;
import java.util.Set;

public class Tuple {

    public final Set<String> c1;

    public final Set<String> c2;

    public Tuple(Set<String> c1, Set<String> c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tuple tuple = (Tuple) o;
        return Objects.equals(c1, tuple.c1) && Objects.equals(c2, tuple.c2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(c1, c2);
    }

    @Override
    public String toString() {
        return "T<" +
               "c1=" + c1 +
               ", c2=" + c2 +
               '>';
    }
}
