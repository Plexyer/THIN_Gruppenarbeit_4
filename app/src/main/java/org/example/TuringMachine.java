package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

public class TuringMachine {
    private final Debug debug = new Debug();
    private final List<String> inputFile = new ArrayList<>();
    private final TextIO textIO = TextIoFactory.getTextIO();
    private final TextTerminal terminal = textIO.getTextTerminal();
    private boolean finished = false;

    public void run() {
        do {
            String choice = textIO.newStringInputReader().read("""
                    1 - Read from file
                    2 - Read from console
                    3 - Exit, you can exit anytime by entering 'exit'
                    
                    Enter your choice: """);

            switch (choice) {
                case "1" -> processFile();
                case "2" -> processGroedernumber(textIO.newStringInputReader().read("Enter Groedernumber: "));
                case "3", "exit" -> finished = true;
                default -> terminal.println("Invalid choice. Please try again.");
            }
        } while (!finished);
        terminal.dispose();
    }

    private void readInFile() {
        try (
                FileReader fileReader = new FileReader("input.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                inputFile.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processGroedernumber(String input) {
        inputEqualsExit(input);
        while (!inputValid(input)) {
            if (inputEqualsExit(input) || finished) return;
            input = textIO.newStringInputReader().read("Invalid input. Please enter a valid Groedernumber:");
        }
        terminal.println("Valid input: " + input);
    }

    private boolean inputValid(String input) {
        String regex = "1((0+1){4}0*11)*((0+1){4}0*)111[01]+";
        Pattern pattern = Pattern.compile(regex);
        return input != null && input.matches(pattern.pattern());
    }

    private void processFile() {
        readInFile();
        for (String line : inputFile) {
            if (inputValid(line)) {
                terminal.println("Valid input from file: " + line);
            } else {
                terminal.println("Invalid input from file: " + line);
            }
        }
    }

    private boolean inputEqualsExit(String input) {
        if (input.equalsIgnoreCase("exit")) {
            finished = true;
        }
        return finished;
    }

}
