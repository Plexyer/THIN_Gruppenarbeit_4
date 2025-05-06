package org.example;

import java.util.ArrayList;
import java.util.List;
import org.beryx.textio.TextTerminal;

public class TuringMachine {

    private final String groedelnumber;
    private final List<Character> band = new ArrayList<>();
    private final TextTerminal terminal;

    public TuringMachine(String groedelnumber, TextTerminal terminal) {
        this.groedelnumber = groedelnumber;
        this.terminal = terminal;
        storeGroedelnumberOnBand();
        prepareBand();
        printBand();
        terminal.println();
    }

    private void storeGroedelnumberOnBand() {
        for (char a : groedelnumber.toCharArray()){
            switch (a) {
                case '1' -> band.add('1');
                case '0' -> band.add('0');
                default -> {
                    terminal.println("Invalid character in Grödelnumber: " + a);
                    return;
                }
            }
        }
    }

    private void prepareBand() {
        for (int i = 0; i < 14; i++) {
            band.addFirst('_');
            band.addLast('_');
        }
    }

    private void printBand() {
        StringBuilder bandString = new StringBuilder();
        for (char c : band) {
            bandString.append(c);
        }
        terminal.println("Band: " + bandString);
    }

}
