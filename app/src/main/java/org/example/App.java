package org.example;

public class App {

    private final TuringMachine turingMachine = new TuringMachine();

    public static void main(String[] args) {
        new App().turingMachine.run();
    }
}
