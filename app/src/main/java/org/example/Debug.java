package org.example;

public class Debug {

    private final boolean debug = true;

    public void printlnDebug(String message) {
        if (debug) System.out.println(message);
    }

}
