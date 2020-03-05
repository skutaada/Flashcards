package flashcards;

public class Main {
    public static void main(String[] args) {
        Flashcards flashcards = new Flashcards(args);

        boolean exit = false;

        while (!exit) {
            flashcards.printAndLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n");
            String answer = flashcards.readAndLog();
            switch (answer) {
                case "add":
                    flashcards.addCard();
                    break;
                case "remove":
                    flashcards.removeCard();
                    break;
                case "import":
                    flashcards.importCards();
                    break;
                case "export":
                    flashcards.exportCards();
                    break;
                case "ask":
                    flashcards.askCards();
                    break;
                case "exit":
                    flashcards.printAndLog("Bye Bye!\n");
                    flashcards.exit();
                    exit = true;
                    break;
                case "log":
                    flashcards.exportLog();
                    break;
                case "hardest card":
                    flashcards.hardestCard();
                    break;
                case "reset stats":
                    flashcards.resetStats();
                    break;
                default:
                    flashcards.printAndLog("Wrong instruction! Try again.\n");
                    break;
            }
        }
    }
}
