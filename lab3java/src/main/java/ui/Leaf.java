package ui;

import java.util.HashMap;

public class Leaf extends Node {

    public Leaf(String label) {
        super(label, new HashMap<>(), label);
    }
}
