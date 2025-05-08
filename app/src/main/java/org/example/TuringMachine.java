package org.example;

import java.util.ArrayList;
import java.util.List;
import org.beryx.textio.TextTerminal;

public class TuringMachine {

    private final String groedelnumber;
    private final List<Character> band = new ArrayList<>();
    private final TextTerminal terminal;
    private int headPosition;
    private int currentState = 1;
    private int acceptState = 2;
    private int stepCount = 0;
    private boolean halted = false;
    private final boolean stepMode;
    private final String blankSymbol = "_";
    private List<Transition> transitions = new ArrayList<>();
    private final Debug debug = new Debug();
    private String inputCode = "";
    private final char[] inputSymbols = {'0', '1', '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public TuringMachine(String groedelnumber, TextTerminal terminal, boolean stepMode) {
        this.groedelnumber = groedelnumber;
        this.terminal = terminal;
        this.stepMode = stepMode;

        storeGroedelnumberOnBand();
        parseTransitions();

        headPosition = 15;

        printInitialConfiguration();
        runMachine();
    }

    private void storeGroedelnumberOnBand() {
        for (char a : groedelnumber.toCharArray()) {
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

    private void parseTransitions() {
        String code;
        if (groedelnumber.startsWith("1")) {
            code = groedelnumber.substring(1);
        }
        else {
            code = groedelnumber;
        }

        int separatorIndex = code.indexOf("111");
        if (separatorIndex == -1) {
            terminal.println("Invalid Grödelnumber format: Missing transition/input separator");
            halted = true;
            return;
        }

        String transitionsCode = code.substring(0, separatorIndex);
        String inputCode = code.substring(separatorIndex + 3);
        this.inputCode = inputCode;

        band.clear();
        for (char c : inputCode.toCharArray()) {
            band.add(c);
        }

        prepareBand();

        createTransitions(transitionsCode + "11");

    }

    private void createTransitions(String transitionsCode) {
        for (int index = 0; index < transitionsCode.length(); ) {
            int nextDelimiter = transitionsCode.indexOf("11", index);
            if (nextDelimiter == -1) break;

            String transitionBlock = transitionsCode.substring(index, nextDelimiter);
            parseTransitionBlock(transitionBlock);

            index = nextDelimiter + 2;
        }
        printAllTransitions();
    }

    private void printAllTransitions() {
        terminal.println("All Transitions:");
        for (Transition t : transitions) {
            terminal.println("Transition: q" + t.fromState + " " + t.readSymbol + " -> q" + t.toState + " " + t.writeSymbol + " " + (t.moveDirection == 1 ? "R" : "L"));
        }
        terminal.println();
    }

    private void prepareBand() {
        for (int i = 0; i < 15; i++) {
            band.addFirst(blankSymbol.charAt(0));
            band.addLast(blankSymbol.charAt(0));
        }
    }

    private void parseTransitionBlock(String block) {
        String[] parts = block.split("1");
        if (parts.length != 5) return;

        int currentState = parts[0].length();
        char currentSymbol = inputSymbols[parts[1].length()-1];
        int newState = parts[2].length();
        char newSymbol = inputSymbols[parts[3].length()-1];
        int direction = parts[4].equals("0") ? -1 : 1; // 0 for left, 1 for right

        transitions.add(new Transition(
                currentState,
                currentSymbol,
                newState,
                newSymbol,
                direction
        ));
    }

    private void printBand() {
        StringBuilder bandString = new StringBuilder();
        int start = 0;
        int end = band.size();

        for (int i = start; i < end; i++) {
            if (i == headPosition) {
                bandString.append("[").append(band.get(i)).append("]");
            } else {
                bandString.append(band.get(i));
            }
        }

        terminal.println("Band:");
        terminal.println(String.valueOf(bandString));
    }

    private void printInitialConfiguration() {
        terminal.println("Starting Turing Machine Emulation");
        terminal.println("Step: 0");
        terminal.println("Current State: q" + currentState);
        terminal.println("Head Position: " + headPosition);
        printBand();
        terminal.println();
    }

    private void runMachine() {
        while (!halted) {
            if (!executeStep()) break;

            if (stepMode) this.sleep(1000);
        }

        if (!stepMode) printConfiguration();

        terminal.println("No more matching transitions found.");
        terminal.println();
        terminal.println("Machine halted.");
        terminal.println("Final result: " + getResultAsString());
        terminal.println("Is in accept state: " + (currentState == acceptState));
        terminal.println();
    }

    private boolean executeStep() {
        char currentSymbol = band.get(headPosition);

        Transition matchingTransition = null;
        for (Transition t : transitions) {
            if (t.fromState == currentState && t.readSymbol == currentSymbol) {
                matchingTransition = t;
                break;
            }
        }

        if (matchingTransition == null) {
            halted = true;
            return false;
        }

        band.set(headPosition, matchingTransition.writeSymbol);
        currentState = matchingTransition.toState;
        headPosition += matchingTransition.moveDirection;
        stepCount++;

        if (stepMode) {
            printConfiguration();
        }

        if (headPosition < 15) {
            extendBandLeft();
        } else if (headPosition >= band.size() - 15) {
            extendBandRight();
        }

        return true;
    }

    private void extendBandLeft() {
        band.addFirst('_');
        headPosition += 1;
    }

    private void extendBandRight() {
        band.addLast('_');
    }

    private void printConfiguration() {
        terminal.println("Step: " + stepCount);
        terminal.println("Current State: q" + currentState);
        terminal.println("Head Position: " + headPosition);
        printBand();
        terminal.println();
    }

    private String getResultAsString() {
        StringBuilder result = new StringBuilder();
        boolean foundNonBlank = false;

        for (char c : band) {
            if (c == '1' || c == '0') {
                result.append(c);
                foundNonBlank = true;
            } else if (foundNonBlank) {
                break;
            }
        }

        return result.toString();
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            terminal.println("Sleep interrupted: " + e.getMessage());
        }
    }


    private static class Transition {
        final int fromState;
        final char readSymbol;
        final int toState;
        final char writeSymbol;
        final int moveDirection;  // -1 for left, 1 for right

        public Transition(int fromState, char readSymbol, int toState, char writeSymbol, int moveDirection) {
            this.fromState = fromState;
            this.readSymbol = readSymbol;
            this.toState = toState;
            this.writeSymbol = writeSymbol;
            this.moveDirection = moveDirection;
        }
    }
}
