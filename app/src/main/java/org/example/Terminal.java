package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import java.math.BigInteger;

public class Terminal {
    private final Debug debug = new Debug();
    private final List<String> inputFile = new ArrayList<>();
    private final TextIO textIO = TextIoFactory.getTextIO();
    private final TextTerminal terminal = textIO.getTextTerminal();
    private boolean finished = false;
    private final String regex = "1?((0+1){4}0*11)*((0+1){4}0*)111[01]+";
    private static boolean stepMode = false;
    private String groedelnumber = "";
    private TuringMachine turingMachine;
    private String[] files = {"aaron.txt", "input.txt", "quadrat.txt"};

    public void run() {
        do {
            String choice = textIO.newStringInputReader().read("""
                    1 - Read from file
                    2 - Read from console
                    3 - Exit, you can exit anytime by entering 'exit'
                    
                    Enter your choice: """);

            switch (choice) {
                case "1" -> chooseFile();
                case "2" -> processGroedelnumber(textIO.newStringInputReader().read("Enter Grödelnumber or Dezimal number: "));
                case "3", "exit" -> finished = true;
                default -> terminal.println("Invalid choice. Please try again.");
            }
        } while (!finished);
        terminal.dispose();
    }

    private void readInFile(String filename) {
        inputFile.clear();
        try (
                var inputStream = this.getClass().getResourceAsStream("/" + filename);
                var reader = new java.io.InputStreamReader(inputStream);
                var bufferedReader = new BufferedReader(reader))
        {
            debug.printlnDebug("File opened successfully: " + filename);
            String line = bufferedReader.readLine();
            if (line == null) {
                terminal.println("File is empty.");
                return;
            }
            debug.printlnDebug("File is not empty.");

            do {
                inputFile.add(line);
                debug.printlnDebug("Line read from file: " + line);
            } while ((line = bufferedReader.readLine()) != null);

        } catch (IOException | NullPointerException e) {
            terminal.println("Error reading file: " + e.getMessage());
        }
    }

    private void processGroedelnumber(String input) {
        inputEqualsExit(input);
        while (!inputValid(input)) {
            if (inputEqualsExit(input) || finished) return;
            input = textIO.newStringInputReader().read("Invalid input. Please enter a valid Grödelnumber or Dezimal number:");
        }


        String modeChoice = textIO.newStringInputReader().read("""
            Choose execution mode:
            1 - Step Mode (pause after each step)
            2 - Run Mode (execute to completion)
            
            Enter your choice: """);
        stepMode = "1".equals(modeChoice);
        inputEqualsExit(modeChoice);
        terminal.println();

        turingMachine = new TuringMachine(groedelnumber, terminal, stepMode);
    }

    private boolean inputValid(String input) {
        Pattern pattern = Pattern.compile(regex);
        assert input != null;
        if (input.matches(pattern.pattern())) {
            groedelnumber = input;
            return true;
        } else {
            if (validInputDecimal(input)) {
                groedelnumber = input;
                terminal.println("Valid input: " + input);
                terminal.println();
                convertDecimalToGroedelnumber(input);
                return true;
            }
            return false;
        }
    }

    private void processFile(String filename) {
        readInFile(filename);
        terminal.println();
        if (inputFile.isEmpty()) {
            terminal.println("No data read from file.");
            return;
        }

        for (String line : inputFile) {
            if (inputValid(line)) {
                processGroedelnumber(groedelnumber);
            } else {
                terminal.println("Invalid input from file: " + line);
            }
        }
    }

    private void chooseFile() {
        terminal.println();
        StringBuilder fileList = new StringBuilder("Available files:\n");
        for (int i = 0; i < files.length; i++) {
            fileList.append(i + 1).append(" - ").append(files[i]).append("\n");
        }
        terminal.println(fileList.toString().trim());

        terminal.println();
        String selection = textIO.newStringInputReader().read("Enter file number:");

        if (inputEqualsExit(selection)) {
            terminal.println();
            return;
        }

        String selectedFile = files[Integer.parseInt(selection) - 1];
        terminal.println();
        terminal.println("Selected file: " + selectedFile);
        processFile(selectedFile);
    }

    private boolean inputEqualsExit(String input) {
        if (input.equalsIgnoreCase("exit")) {
            finished = true;
        }
        return finished;
    }

    private boolean validInputDecimal(String input) {
        try {
            BigInteger number = new BigInteger(input.trim());
            if (number.compareTo(BigInteger.ZERO) < 0) {
                terminal.println("Invalid input. Please enter a non-negative integer.");
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            terminal.println("Error: " + e.getMessage());
            return false;
        }
    }

    private void convertDecimalToGroedelnumber(String number) {
        StringBuilder groedelnumber = new StringBuilder();
        BigInteger decimalNumber = new BigInteger(number);
        while (decimalNumber.compareTo(BigInteger.ZERO) > 0) {
            int remainder = decimalNumber.mod(BigInteger.valueOf(2)).intValue();
            groedelnumber.insert(0, remainder);
            decimalNumber = decimalNumber.divide(BigInteger.valueOf(2));
        }
        this.groedelnumber = groedelnumber.toString();
        terminal.println("Converted decimal to Grödelnumber: " + groedelnumber);
        terminal.println();
    }

}
