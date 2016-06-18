package newdeal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;
import static java.lang.Thread.sleep;

/**
 * DealOrNoDealGUI Class creates and populates a JFrame with the functionality
 * of the deal or no deal game. We decided it would be wiser to use our CLI class as the model
 * for our GUI game so that we don't have to rewrite the functionality that the intial CLI version provides.
 * This class/main works by first welcoming a user (getting the username).
 * Then the user selects a case that holds a prize (getting playerscase).
 * Then the game is initiated.
 * The game is played by opening cases, which then updates the view to make the prize that was inside the case go pink 
 * on the right and left side depending on the value of the prize. UpdateCases, UpdatePrize1, UpdatePrize2, UpdateBottomPanel
 * After a certain number of cases have been opened the player is offered a prize from the banker. BankOffer
 * If the player likes the value they can select deal where they will receive their prize, if they don't like the value they
 * can select no deal which will trigger them to go back to opening more cases. Deal
 *
 * @author Andre Cowie 14862344
 * @author Tony van Swet 0829113
 *
 * @version 1/06/2016
 */
public class DealorNoDealGUI {

    public JFrame frame;
    public DealOrNoDeal game;
    public JPanel view;
    public String username;
    public int playersCase;
    private Toolkit kit;
    public int count;
    
    //Deal or no deal gui containing a frame, view, a game and also the players username and selected case.
    public DealorNoDealGUI() {
        count = 0;
        this.username = "";
        frame = new JFrame("Deal Or No Deal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        kit = Toolkit.getDefaultToolkit();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); //setSize(kit.getScreenSize().width, kit.getScreenSize().height); Changed this to fit to screen with taskbar
        view = new JPanel(new BorderLayout());
        frame.add(view);
        welcome();
    }

    //Is the offer from the banker accepted, If so congratulate, if not keep playing.
    public void Deal(boolean x) {
        view.removeAll();
        if (x) {
            JPanel youWon = new JPanel(new BorderLayout());
            JLabel congrats = new JLabel("<html>Congratulations you won: $" + game.Offer() + "<br><div style='text-align: center;'> From Case " + (playersCase + 1) + "</html>", SwingConstants.CENTER);
            congrats.setFont(congrats.getFont().deriveFont(60.0f));
            youWon.add(congrats, BorderLayout.CENTER);
            view.add(youWon, BorderLayout.CENTER);
            DBInteractions interact = new DBInteractions();
            interact.establishConnection();
            interact.savePlayersGame(username, playersCase+1, game.Offer());
        } else {
            JPanel fresh = new JPanel(new BorderLayout());
            fresh.add(updatePrize1(), BorderLayout.WEST);
            fresh.add(updateCases(), BorderLayout.CENTER);
            fresh.add(updatePrize2(), BorderLayout.EAST);
            fresh.add(bottomPanel(), BorderLayout.SOUTH);
            view.add(fresh);
        }
        view.updateUI();
    }

    //Give the player an offer based on an average of the prizes that are still available in the prize pool. Player can accept or decline.
    public void BankOffer() {
        JPanel dond = new JPanel(new BorderLayout());
        JPanel ynae = new JPanel();
        JLabel offer = new JLabel("The Banker Offers You: $" + game.Offer(), SwingConstants.CENTER);
        offer.setFont(offer.getFont().deriveFont(60.0f));
        JButton yes = new JButton("Deal");
        yes.setPreferredSize(new Dimension(200, 69));
        JButton no = new JButton("No Deal");
        no.setPreferredSize(new Dimension(200, 69));
        yes.setActionCommand("true");
        no.setActionCommand("false");
        class DoND implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent ae) {
                view.removeAll();
                Deal(Boolean.parseBoolean(ae.getActionCommand()));
            }
        }
        yes.addActionListener(new DoND());
        no.addActionListener(new DoND());
        dond.add(offer, BorderLayout.CENTER);
        ynae.add(yes);
        ynae.add(no);
        dond.add(ynae, BorderLayout.SOUTH);
        view.add(dond, BorderLayout.CENTER);
        view.updateUI();
    }
    
    //Update the buttons available to open the cases that are still shut. On a button click the case is opened removing the prize from the prize pool.
    public JPanel updateCases() {
        JPanel updated_panel = new JPanel(new GridLayout(5, 5));
        updated_panel.setPreferredSize(new Dimension(kit.getScreenSize().width / 2, kit.getScreenSize().height));
        JButton[] case_clicks = new JButton[26];

        class OpenCase implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent ae) {
                int caseToOpen = Integer.parseInt(ae.getActionCommand());
                view.removeAll();

                game.getCases()[caseToOpen].setOpen(true);
                JPanel fresh = new JPanel(new BorderLayout());
                JPanel updatedBottom = bottomPanel();
                updatedBottom.add(recentOpen(caseToOpen));
                fresh.add(updatePrize1(), BorderLayout.WEST);
                fresh.add(updateCases(), BorderLayout.CENTER);
                fresh.add(updatePrize2(), BorderLayout.EAST);
                fresh.add(updatedBottom, BorderLayout.SOUTH);
                count++;
                if (count == 25) {
                    fresh.removeAll();

                    ImageIcon image = new ImageIcon("raining-money.jpg");//Make sure this is in your project folder
                    JLabel label = new JLabel("", image, JLabel.CENTER);
                    JPanel endPanel = new JPanel(new BorderLayout());
                    endPanel.add(label, BorderLayout.CENTER);
                    JLabel win = new JLabel("Congratulations you won: $" + game.Offer(), SwingConstants.CENTER);
                    win.setFont(label.getFont().deriveFont(64.0f));
                    endPanel.add(win, BorderLayout.SOUTH);
                    fresh.add(endPanel);
                    DBInteractions interact = new DBInteractions();
                    interact.establishConnection();
                    interact.savePlayersGame(username, playersCase+1, game.Offer());
                }
                if (count == 6 || count == 11 || count == 15 || count == 18 || count == 20 || count == 22 || count == 23 || count == 24) {
                    BankOffer();
                } else {

                    view.add(fresh);
                    view.updateUI();
                }

            }

        }

        for (int i = 0; i < 26; i++) {
            if (game.getCases()[i].isOpen() || game.getCases()[i].isSelected()) {
            } else {
                case_clicks[i] = new JButton("" + (i + 1));
                case_clicks[i].setActionCommand("" + i);
                case_clicks[i].addActionListener(new OpenCase());
                updated_panel.add(case_clicks[i]);
            }
        }

        return updated_panel;
    }

    //The prize contained within the case that was most recently opened.
    public JLabel recentOpen(int caseNum) {
        JLabel caseContained = new JLabel("| Case Contained: $" + game.getCases()[caseNum].getDollarsInside().toString());
        caseContained.setFont(caseContained.getFont().deriveFont(30.0f));
        return caseContained;
    }

    //The bottom panel contains information about the user playing the game, the case that is in their possession and the prize that was in the most recently opened case.
    public JPanel bottomPanel() {
        JPanel bottom_panel = new JPanel();
        JLabel name = new JLabel("Player: " + username);
        JLabel caseSelected = new JLabel("| Your Case: " + (playersCase + 1));
        name.setFont(name.getFont().deriveFont(30.0f));
        caseSelected.setFont(caseSelected.getFont().deriveFont(30.0f));
        bottom_panel.add(name);
        bottom_panel.add(caseSelected);
        return bottom_panel;
    }

    //Update the prize pool on the left, dark prize remains in an unopened case, pink the prize was in a case that has been opened. 
    public JPanel updatePrize1() {
        JPanel prize1 = new JPanel();
        prize1.setLayout(new BoxLayout(prize1, BoxLayout.Y_AXIS));
        prize1.setPreferredSize(new Dimension(kit.getScreenSize().width / 4, kit.getScreenSize().height));
        JLabel[] prizeLabels = new JLabel[13];
        for (int x = 0; x < 13; x++) {
            for (int i = 0; i < 26; i++) {
                if (game.getCases()[i].getDollarsInside() == game.prizes[x]) {
                    if (game.getCases()[i].isOpen()) {
                        prizeLabels[x] = new JLabel("$" + game.prizes[x]);
                        prizeLabels[x].setForeground(Color.pink);
                        prizeLabels[x].setFont(prizeLabels[x].getFont().deriveFont(50.0f));

                    } else {
                        prizeLabels[x] = new JLabel("$" + game.prizes[x], SwingConstants.RIGHT);
                        prizeLabels[x].setFont(prizeLabels[x].getFont().deriveFont(50.0f));

                    }

                }
            }

            prize1.add(prizeLabels[x]);
        }
        return prize1;
    }

    //Update the prize pool on the right, dark prize remains in an unopened case, pink the prize was in a case that has been opened. 
    public JPanel updatePrize2() {
        JPanel prize2 = new JPanel();
        prize2.setLayout(new BoxLayout(prize2, BoxLayout.Y_AXIS));
        prize2.setPreferredSize(new Dimension(kit.getScreenSize().width / 4, kit.getScreenSize().height));
        JLabel[] prizeLabels = new JLabel[13];

        for (int x = 13; x < 26; x++) {
            for (int i = 0; i < 26; i++) {
                if (game.getCases()[i].getDollarsInside() == game.prizes[x]) {
                    if (game.getCases()[i].isOpen()) {
                        prizeLabels[x - 13] = new JLabel("$" + game.prizes[x], SwingConstants.RIGHT);
                        prizeLabels[x - 13].setForeground(Color.pink);
                        prizeLabels[x - 13].setFont(prizeLabels[x - 13].getFont().deriveFont(50.0f));
                        prizeLabels[x - 13].setAlignmentX(50);//Pushes each JLabel to the right
                    } else {
                        prizeLabels[x - 13] = new JLabel("$" + game.prizes[x], SwingConstants.RIGHT);
                        prizeLabels[x - 13].setFont(prizeLabels[x - 13].getFont().deriveFont(50.0f));
                        prizeLabels[x - 13].setAlignmentX(50);
                    }
                }
            }
            prize2.add(prizeLabels[x - 13]);
        }
        return prize2;
    }

    //Intiate the deal or no deal game, the game is controlled by the actionlisteners on the buttons within the panels.
    public void intiateGame() {
        game = new DealOrNoDeal(username, playersCase);
        view.add(updateCases(), BorderLayout.CENTER);
        view.add(updatePrize1(), BorderLayout.WEST);
        view.add(updatePrize2(), BorderLayout.EAST);
        view.add(bottomPanel(), BorderLayout.SOUTH);
        view.updateUI();
    }
        
    //Choose a case that you think has the highest value prize within. There are 26 cases.
    public void choseACase() {
        JLabel a = new JLabel("Pick a case.");
        a.setFont(a.getFont().deriveFont(30.0f));
        JPanel caseview = new JPanel();
        JPanel casess = new JPanel();
        casess.setSize(kit.getScreenSize().width/2,kit.getScreenSize().height/2);
        caseview.add(a, BorderLayout.NORTH);
        GridLayout buttons = new GridLayout(3, 10);
        casess.setLayout(buttons);
        JButton[] cases = new JButton[26];
        class CaseSelection implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String output = ae.getActionCommand();
                playersCase = Integer.parseInt(output);
                view.removeAll();
                view.updateUI();
                intiateGame();
            }

        }
        for (int x = 0; x < 26; x++) {
            cases[x] = new JButton("" + (x + 1));
            cases[x].setPreferredSize(new Dimension(125, 100));
            cases[x].setActionCommand("" + x);
            cases[x].addActionListener(new CaseSelection());
            casess.add(cases[x], BorderLayout.CENTER);
        }
        caseview.add(casess, BorderLayout.CENTER);
        DBInteractions interact = new DBInteractions();
        interact.establishConnection();
        JTextArea b = new JTextArea(interact.newPlayerLoad(username));
        b.setEditable(false);
        b.setPreferredSize(new Dimension(333, 200));
        b.setLineWrap(true);
        caseview.add(b, BorderLayout.SOUTH);
        view.add(caseview);
        view.updateUI();
    }
    
    //Welcome the player to the game and ask for their name.
    public void welcome() {
        JLabel welcome = new JLabel("Welcome to deal or no deal!", JLabel.CENTER);
        welcome.setFont(welcome.getFont().deriveFont(40.0f));
        view.add(welcome);
        try {
            sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DealorNoDealGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JTextField userinput = new JTextField(20);

        userinput.setPreferredSize(new Dimension(100, 69));//Change text field size, if you change the text field font size it autochanges the size

        JButton b = new JButton("Enter");
        b.setPreferredSize(new Dimension(200, 69)); //Changed button size
        class WelcomeAction implements ActionListener {

            JTextField textFieldStore;

            public WelcomeAction(JTextField textFieldInput) {
                textFieldStore = textFieldInput;
            }

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (textFieldStore.getText().length() < 1) {

                } else {
                    updateUser();
                    view.removeAll();
                    view.updateUI();
                    choseACase();
                }
            }

            public void updateUser() {
                username = textFieldStore.getText();
            }

        }
        userinput.addActionListener(new WelcomeAction(userinput));//Now accepts escape char and clicking on enter
        b.addActionListener(new WelcomeAction(userinput));
        welcome.setText("What is your name?");
        
        JPanel subPanel = new JPanel();
        subPanel.add(userinput);
        subPanel.add(b);
        view.add(subPanel, BorderLayout.SOUTH);

    }
    
    //Play the GUI version of deal or no deal.
    public static void main(String[] args) {
        new DealorNoDealGUI();
    }
}
