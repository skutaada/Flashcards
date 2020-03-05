package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

class Flashcards {

    private Map<String, String> cards = new LinkedHashMap<>();
    private Map<String, String> cardsInverse = new LinkedHashMap<>();
    private Map<String, Integer> cardMistakes = new HashMap<>();

    private List<String> log = new ArrayList<>();

    private boolean exportEnd = false;
    private String exportFileName;

    public Scanner sc = new Scanner(System.in);

    public Flashcards(String[] args) {
        if (args.length == 2) {
            if (args[0].equals("-import")) {
                importFile(args[1]);
            } else if (args[0].equals("-export")) {
                exportEnd = true;
                exportFileName = args[1];
            }
        } else if (args.length == 4) {
            if (args[0].equals("-import")) {
                importFile(args[1]);
            } else if (args[0].equals("-export")) {
                exportEnd = true;
                exportFileName = args[1];
            }
            if (args[2].equals("-import")) {
                importFile(args[3]);
            } else if (args[2].equals("-export")) {
                exportEnd = true;
                exportFileName = args[3];
            }
        }
    }

    public void setLog(String logLine) {
        this.log.add(logLine);
    }

    public void printAndLog(String message) {
        System.out.print(message);
        setLog(message);
    }

    public String readAndLog() {
        String input = sc.nextLine();
        setLog(input);
        return input;
    }

    // Adds card and definition from user input
    public void addCard() {
        printAndLog("The card:\n");
        String card = readAndLog();
        if (cards.containsKey(card)) {
            printAndLog(String.format("The card \"%s\" already exists.\n", card));
        } else {
            printAndLog("The definition of the card:\n");
            String def = readAndLog();
            if (cards.containsValue(def)) {
                printAndLog(String.format("The definition \"%s\" already exists.\n", def));
            } else {
                cards.put(card, def);
                cardsInverse.put(def, card);
                printAndLog(String.format("The pair (\"%s\":\"%s\") has been added.\n", card, def));
            }
        }
    }

    // Removes card and its definition from the set
    public void removeCard() {
        printAndLog("The card:\n");
        String card = readAndLog();
        String def = cards.get(card);
        if (def == null) {
            printAndLog(String.format("Can't remove \"%s\": there is no such card.\n", card));
        } else {
            cards.remove(card);
            cardsInverse.remove(def);
            cardMistakes.remove(card);
            printAndLog("The card has been removed.\n");
        }
    }

    // Imports cards from text file. The format is "card:def\n"
    public void importCards() {
        printAndLog("File name:\n");
        String fileName = readAndLog();

        importFile(fileName);
    }

    private void importFile(String fileName) {
        int cardCounter = 0;

        File file = new File(fileName);

        try (Scanner importScanner = new Scanner(file)) {
            while (importScanner.hasNext()) {
                String[] card = importScanner.nextLine().split(":");
                cards.put(card[0], card[1]);
                cardsInverse.put(card[1], card[0]);
                cardMistakes.put(card[0], Integer.parseInt(card[2]));
                cardCounter++;
            }
            printAndLog(String.format("%d cards have been loaded.\n", cardCounter));
        } catch (FileNotFoundException e) {
            printAndLog("File not found.\n");
        }
    }

    // Exports cards to a file manually
    public void exportCards() {
        printAndLog("File name:\n");
        String fileName = readAndLog();

        exportFile(fileName);
    }

    private void exportFile(String fileName) {
        File file = new File(fileName);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (var card : cards.entrySet()) {
                printWriter.printf("%s:%s:%d\n", card.getKey(), card.getValue(), cardMistakes.getOrDefault(card.getKey(), 0));
            }
            printAndLog(String.format("%d cards have been saved.\n", cards.size()));
        } catch (FileNotFoundException e) { }
    }

    // Ask the definitions of the cards
    public void askCards() {
        printAndLog("How many times to ask?\n");
        final int times = sc.nextInt();
        setLog(Integer.toString(times));
        sc.nextLine();

        int j = 0;

        while (j < times) {
            for (var entry : cards.entrySet()) {
                printAndLog(String.format("Print the definition of \"%s\":\n", entry.getKey()));
                String def = readAndLog();
                if (Objects.equals(entry.getValue(), def)) {
                    printAndLog("Correct answer\n");
                } else if (cards.containsValue(def)) {
                    String correctCard = cardsInverse.get(def);
                    printAndLog(String.format("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\".\n",
                            entry.getValue(), correctCard));
                    cardMistakes.put(entry.getKey(), cardMistakes.getOrDefault(entry.getKey(), 0) + 1);
                } else {
                    printAndLog(String.format("Wrong answer. The correct one is \"%s\".\n", entry.getValue()));
                    cardMistakes.put(entry.getKey(), cardMistakes.getOrDefault(entry.getKey(), 0) + 1);
                }
                j++;
                if (j == times) {
                    break;
                }
            }
        }
    }

    public void exportLog() {
        printAndLog("File name:\n");
        String fileName = readAndLog();

        File file = new File(fileName);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (String logLine : log) {
                printWriter.print(logLine);
            }
            printAndLog("The log has been saved\n");
        } catch (FileNotFoundException e) { }
    }

    public void hardestCard() {
        int mostErrors = 0;
        List<String> hardest = new ArrayList<>();

        for (var entry : cardMistakes.entrySet()) {
            if (entry.getValue() >= mostErrors) {
                hardest.add(entry.getKey());
                mostErrors = entry.getValue();
            }
        }

        if (mostErrors == 0) {
            printAndLog("There are no cards with errors.\n");
        } else {
            printAndLog("The hardest cards are ");
            if (hardest.size() == 1) {
                printAndLog(String.format("The hardest card is \"%s\". You have %d errors answering it.\n", hardest.get(0), mostErrors));
            } else {
                for (int i = 0; i < hardest.size() - 1; i++) {
                    printAndLog(String.format("\"%s\", ", hardest.get(i)));
                }
                printAndLog(String.format("\"%s\". You have %d errors answering them.\n", hardest.get(hardest.size() - 1), mostErrors));
            }
        }
    }

    public void resetStats() {
        cardMistakes.clear();
        printAndLog("Card statistics has been reset.\n");
    }

    public void exit() {
        if (exportEnd) {
            exportFile(exportFileName);
        }
    }
}
