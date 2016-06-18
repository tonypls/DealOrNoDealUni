package newdeal;


/**
 * DealOrNoDeal
 * 
 * This is a game that is played on TV commonly, the goal is to walk away with the biggest prize possible.
 * The player selects a case to put in their possession they then begin opening cases.
 * Each case contains a prize.
 * When a case is opened the prize is no longer available.
 * As the player opens the cases, the banker gives offers based on the remaining prizes in the unopened cases.
 * The player can accept or decline the bankers offer.
 * If the player declines all the bankers offers they will receive the prize that is in the case they originally chose to possess.
 * 
 * Created by 
 * @author Andre Cowie 14862344 on 16/03/2016
 * @author Tony van Swet 0829113 5/04/2016
 * 
 */
import java.util.*;

public class DealOrNoDeal {
    static Integer[] prizes = {0, 1, 2, 5, 10, 20, 50, 100, 150, 200, 500, 750, 1000, 2000, 3000, 4000, 5000, 10000, 15000, 20000, 30000, 50000, 75000, 100000, 200000, 500000};
    private Case[] cases = new Case[26];
    Case[] sortedCases = new Case[26];
    private Player contestant;
    private int selectedCase;

    public static Scanner scan = new Scanner(System.in);

    //A game of deal or no deal is started once a player has identifyed them self and has chosen a case for their possession.
    //On the creation of the game the prizes are shuffled and put into the cases.
    public DealOrNoDeal(String _contestantName, int _selectedCase) {
        this.contestant = new Player(_contestantName);
        setSelectedCase(_selectedCase);
        ArrayList<Integer> prize = new ArrayList<Integer>(Arrays.asList(prizes));
        for(int i=0; i < 26; i++){
            this.sortedCases[i] = new Case();
            this.sortedCases[i].setDollarsInside(prize.get(i));
        }
        Collections.shuffle(prize);
        for (int i = 0; i < 26; i++) {
            this.cases[i] = new Case();
            this.cases[i].setDollarsInside(prize.get(i));
        }
        this.cases[_selectedCase].setSelected(true);
    }

    //An offer from the banker based on the average from prizes that remain in unopened cases.
    public int Offer() {
        int totalAccumulated = 0;
        int remainingUnopened = 0;
        for (int i = 0; i < 26; i++) {
            if (!this.cases[i].isOpen()) {
                totalAccumulated = totalAccumulated + this.cases[i].getDollarsInside();
                remainingUnopened++;
            }
        }
        return totalAccumulated / remainingUnopened;
    }

    //Whether the player accepts the offer they have been given... DEAL or NO DEAL?
    public boolean isItADeal() {
        System.out.println("Great you have opened enough cases for the banker to give you an offer.");
        int bankersDeal = this.Offer();
        System.out.println("The banker gave you an offer of $" + bankersDeal);
        System.out.println("(D)eal or (N)o Deal?");
        scan.nextLine();
        String soDeal = scan.nextLine();
        switch (soDeal.toUpperCase()) {
            case "D":
                break;
            case "N":
                break;
            default:
                System.out.println("please enter again ");
                boolean repeat = true;

                while (repeat) {
                    System.out.println("(D)eal or (N)o Deal");
                    soDeal = scan.nextLine();

                    switch (soDeal.toUpperCase()) {
                        case "D":
                            repeat = false;
                            break;
                        case "N":
                            repeat = false;
                            break;
                    }
                }
                break;
        }
        if (soDeal.charAt(0) == 'D' || soDeal.charAt(0) == 'd') {
            System.out.println("DEAL!\nYou won $" + bankersDeal + "\nSpend it wisely.");
            return true;
        } else {
            System.out.println("NO DEAL!");
            return false;
        }
    }

    //Open a case...
    public void openCase(int caseNumber) {
        this.cases[caseNumber].setOpen(true);
        System.out.println("You opened case number: " + (caseNumber + 1));
        System.out.println("It contained: $" + this.cases[caseNumber].getDollarsInside());
    }

    //Select a case to open. Once selected open that case in the method above.
    public void pickACase() {
        System.out.println("Pick a case!");
        printClosedCases();
        try {
            int selection = (scan.nextInt() - 1);
            while (selection == this.getSelectedCase()) {
                System.out.println("You can not open your case just yet, pick another.");
                selection = (scan.nextInt() - 1);
            }
            while (this.cases[selection].isOpen()) {
                System.out.println("You have already opened that case, pick another.");
                selection = (scan.nextInt() - 1);
            }
            this.openCase(selection);
        } catch (Exception e) {
            System.out.println("That is not a valid case!");
            scan.nextLine();
            pickACase();
        }
    }

    //The status of the prizes, whether they still remain in unopened cases or whether the cases they resided in have been opened.
    public String printPrizeStatuss() {
        String openedCases = "Opened Prizes: ";
        String closedCases = "\nPrizes Still Available: ";
        for (int i = 0; i < 26; i++) {
            for(int x = 0; x < 26; x ++){
                if(cases[x].getDollarsInside() == prizes[i].intValue()){
                    if(cases[x].isOpen()){
                        openedCases += prizes[i].intValue();
                        openedCases += ", ";
                    } else{
                        closedCases += prizes[i].intValue();
                        closedCases += ", ";
                    }
                }
            }
        }
        return openedCases.substring(0, openedCases.length() - 2) + closedCases.substring(0, closedCases.length() - 2);
    }

    //Print the cases that are closed and waiting to be opened, not including the case in the players possession.
    public void printClosedCases() {
        System.out.print("Available Cases: ");
        for (int i = 0; i < 26; i++) {
            if (!(cases[i].isOpen())) {
                if(!cases[i].isSelected())
                System.out.print("["+(i+1)+"]");
            }
        }
        System.out.println(" ");
    }

    //Welcome the player to Deal or no deal and get the identity of the player.
    public static String welcomePlayer(){
        System.out.println("Welcome to deal or no deal!");
        System.out.println("What is your name?");
        String playerName = scan.nextLine();
        while (playerName.length() < 1) {
            System.out.println("Please enter a valid name");
            playerName = scan.nextLine();
        }
        System.out.print("We wish you good luck " + playerName + ".\nNow lets get underway, we have twenty-six cases each containing potential prize that you could win!\n");
        return playerName;
    }
    
    //Select the case the player wants.
    public static int selectContestantsCase(){
        int playersCase = 0;

        while(playersCase < 1 || playersCase > 26){
            System.out.println("Which case would you like to select? (1-26)");
            while (!scan.hasNextInt()) {

                System.out.println("Enter a valid number");
                scan.next();
            }
            playersCase = scan.nextInt();
        }

        playersCase--;
        return playersCase;
    }

    //Open a number of cases then get a bank offer.
    public boolean openSomeCases(int howMany){
        System.out.println("Great you will now open "+howMany+" cases in a row and then the banker will give you an offer");
        for (int i = 0; i < howMany; i++) {
            pickACase();
        }
        System.out.println(this.printPrizeStatuss());
        if (this.isItADeal()) {
            return true;
        }else{
            return false;
        }
    }

    //The flow of our deal or no deal game. Welcome player then let the player select their case. Then open cases and get offers.
    //If the player reaches the last two cases they are given a final offer, if they don't accept they get the value in their case.
    public static void main(String[] args) {
        DBInteractions interact = new DBInteractions();
        String playerName = welcomePlayer();
        int playersCase = selectContestantsCase();
        interact.establishConnection();
        interact.createGamesTable();
        DealOrNoDeal game = new DealOrNoDeal(playerName, playersCase);
        System.out.println(interact.newPlayerLoad(playerName));
        System.out.println("Its time to play deal or no deal!!!");
        
        for(int i = 6; i > 0;i--){
            if(i==2 || i==1){
                if(game.openSomeCases(i)){
                    interact.savePlayersGame(playerName, playersCase, game.Offer());
                    return;
                }
                if(game.openSomeCases(i)){
                    interact.savePlayersGame(playerName, playersCase, game.Offer());
                    return;
                }
                if (i == 1){
                    System.out.println("Wow you have mad it to the end of the game there are only two prizes left!\nAre you sure you don't want this offer?");
                    System.out.println("$"+game.Offer());
                    System.out.println("(D)eal or (N)o Deal?");
                    String soDeal;
                    do {
                        soDeal =scan.nextLine();
                    } while (!(soDeal.toUpperCase().charAt(0) == 'D' || soDeal.toUpperCase().charAt(0) == 'N'));
                    if (soDeal.charAt(0) == 'D') {
                        System.out.println("DEAL!\nYou won $" + game.Offer() + "\nSpend it wisely.");
                        interact.savePlayersGame(playerName, playersCase+1, game.Offer());
                    } else {
                        System.out.println("NO DEAL!");
                        System.out.println("Time to open your case!\nIt contains: $" + game.cases[playersCase].getDollarsInside() + "\nCongratulations. Well played.");
                        interact.savePlayersGame(playerName, playersCase+1, game.cases[playersCase].getDollarsInside());
                    }
                }
            }else{
                if(game.openSomeCases(i)){
                    interact.savePlayersGame(playerName, playersCase+1, game.Offer());
                    return;
                }
            }
        }
    }
    
    //Below are some methods to allow for encapsulation of the deal or no deals key objects, some of these methods are utilized by the GUI class.

    public Case[] getCases() {
        return cases;
    }

    public void setCases(Case[] cases) {
        this.cases = cases;
    }

    public int getSelectedCase() {
        return selectedCase;
    }

    public void setSelectedCase(int selectedCase) {
        this.selectedCase = selectedCase;
    }

    public Player getContestant() {
        return contestant;
    }
}
