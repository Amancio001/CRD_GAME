package PSI18;

import jade.core.AID;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.border.Border;

public class PSI18_GUI extends JFrame implements ActionListener {

    private JLabel label;
    JTextArea console;
    JScrollPane scroll;
    private boolean verbose = true;
    public static GameInfo gameInfo;
    private JPanel players = new JPanel();
    private JMenuItem removePlayersItem;
    private int nOfPlayers = 0;
    private MainAgent mainAgent;
    public boolean stop = false;
    public PSI18_GUI(MainAgent mainAgent) {
        super("Collective-Risk Dilemma Game (PSI 18)");
        this.mainAgent = mainAgent;
        this.setBackground(Color.lightGray);
        this.setForeground(Color.black);

        JMenu editMenu = new JMenu( "Edit");
            JMenuItem itemResetPlayers = new JMenuItem("Reset Players");
            JMenuItem itemRemPlayer    = new JMenu("Remove player");
            removePlayersItem = itemRemPlayer;
            editMenu.add(itemResetPlayers);
            editMenu.add(itemRemPlayer);

            itemResetPlayers.addActionListener(this);
            itemRemPlayer.addActionListener(this);

        JMenu runMenu = new JMenu( "Run");
            JMenuItem itemNew         = new JMenuItem("New");
            JMenuItem itemStop        = new JMenuItem("Stop");
            JMenuItem itemContinue    = new JMenuItem("Continue");
            JMenuItem itemChangeParam = new JMenu("Change parameters");
                JMenuItem itemDisasterProbability = new JMenuItem("Disaster probability");
                JMenuItem itemNumberOfGames = new JMenuItem("Number of games");
                itemChangeParam.add(itemDisasterProbability);
                itemChangeParam.add(itemNumberOfGames);
                itemDisasterProbability.addActionListener(this);
                itemNumberOfGames.addActionListener(this);
            runMenu.add(itemNew);
            runMenu.add(itemStop);
            runMenu.add(itemContinue);
            runMenu.add(itemChangeParam);

            itemNew.addActionListener(this);
            itemStop.addActionListener(this);
            itemContinue.addActionListener(this);
            itemChangeParam.addActionListener(this);

        JMenu windowMenu = new JMenu("Window");
            JCheckBoxMenuItem itemVerbose = new JCheckBoxMenuItem("Verbose On/Off");
            itemVerbose.setSelected(true);
            windowMenu.add(itemVerbose);

            itemVerbose.addActionListener(this);

        JMenu helpMenu = new JMenu("Help");
            JMenuItem itemAbout = new JMenuItem("About");
            helpMenu.add(itemAbout);
            itemAbout.addActionListener(this);

        JMenuBar menuBar = new JMenuBar();
            menuBar.setBackground(Color.cyan);
            menuBar.add(editMenu);
            menuBar.add(runMenu);
            menuBar.add(windowMenu);
            menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);
        this.setLayout(new GridLayout(1,3));
        resetPlayers();
        this.add(players);
        GameInfo gameInfoFrame = new GameInfo();
        this.gameInfo = gameInfoFrame;
        this.add(gameInfoFrame.getPanel());
        this.console = new JTextArea(1, 1);
        scroll = new JScrollPane (console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setAutoscrolls(true);
        scroll.setFocusCycleRoot(true);
        console.setEditable(false);
        console.setFocusCycleRoot(true);
        console.setAutoscrolls(true);
        this.add(scroll);
        this.setSize (new Dimension(1000,800));     // Window size
        this.setLocation (new Point (100, 100));
        this.setVisible(true);
        return;
    }

    public void consoleLog(String message){
        if(!this.verbose) return;
        this.console.append(message + "\n");
        console.setCaretPosition(console.getDocument().getLength());
        return;
    }

    public void consoleLog(String message, boolean important){
        if(important){
            this.console.append(message + "\n");
            console.setCaretPosition(console.getDocument().getLength());
            return;
        }
        else consoleLog(message);
    }

    public Player addPlayer(int playerID, int remainingBudget, AID aid) throws Exception{
        if(nOfPlayers == 5){
            throw new Exception("Cant add more players");
        }
        Player newPlayer = new Player(playerID, remainingBudget, aid);
        players.add(newPlayer.getPlayerPanel(), nOfPlayers, nOfPlayers );
        nOfPlayers++;
        return newPlayer;
    }

    public void resetPlayers() {
        players.removeAll();
        players.setLayout(new GridLayout(5,1, 5,5));
        nOfPlayers = 0;
    }

    public void updatePlayerRemover(){
        this.removePlayersItem.removeAll();
        Iterator<PSI18_GUI.Player> players = mainAgent.getPlayers().iterator();;
        while (players.hasNext()){
            Player player = players.next();
            JMenuItem itemPlayer = new JMenuItem("Remove Player " + player.playerID);
            itemPlayer.addActionListener(this);
            this.removePlayersItem.add(itemPlayer);
        }
    }

    public void actionPerformed (ActionEvent evt) {
        String event = evt.getActionCommand();
        if(event.contains("Verbose")) {
            this.verbose = !this.verbose;
            if (!this.verbose) {
                this.console.append("[SYSTEM]: Verbose Off, I'll stay quiet\n");
            } else {
                this.console.append("[SYSTEM]: Verbose On!\n");
            }
            return;
        }
        this.console.append("[USER ACTION]: Selected *" + event + "* option\n");
        if(event.contains("Remove Player")){
            int id = Integer.parseInt(event.split(" ")[2]);
            Iterator<PSI18_GUI.Player> players = mainAgent.getPlayers().iterator();
            Player playerToRemove = null;
            while (players.hasNext()){
                Player player = players.next();
                if (player.playerID == id){
                    playerToRemove = player;
                }
            }
            this.players.remove(id);
            mainAgent.removePlayer(playerToRemove);

            this.updatePlayerRemover();
            this.revalidate();
        }
        switch(event) {
            case "Number of games":
                new MyDialog(this, "Number of games", true, this);
                break;
            case "Disaster probability":
                new MyDialog(this, "Disaster probability", true, this);
                break;
            case "About":
                this.console.append("\nCollective-Risk Dilemma Game (PSI 18)\n\n");
                this.console.append("Author: Amancio Pontes Hermo\n");
                this.console.append("Version: 0.1 (interface only)\n");
                this.console.append("Contact: amancio001@gmail.com\n\n");
                break;
            case "Reset Players":
                mainAgent.updatePlayers();
                break;
            case "New":
                mainAgent.newGame();
                break;
            case "Stop":
                 stop = true;
                break;
            case "Continue":
                stop = false;
                break;
        }
    }

    private synchronized void awakeAll() {
        this.notifyAll();
    }

    public class GameInfo {

        int players = 0;
        int round = 0;
        int gamesPlayed = 0;
        int gamesToPlay = 10;
        int threshold = 0;
        float disasterProbability = 0.8f;
        boolean disasterInThisRound = false;

        public void setGamesToPlay(int gamesToPlay) {
            this.gamesToPlay = gamesToPlay;
            this.updateUI();
        }

        public void setPlayers(int players) {
            this.players = players;
            this.updateUI();
        }

        public void setRound(int round) {
            this.round = round;
            this.updateUI();
        }

        public void setGamesPlayed(int gamesPlayed) {
            this.gamesPlayed = gamesPlayed;
            this.updateUI();
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
            this.updateUI();
        }

        public void setDisasterProbability(float disasterProbability) {
            this.disasterProbability = disasterProbability;
            this.updateUI();
        }


        /**
         * Sets one round as played
         * @return false if all rounds where played, true otherwise
         */
        public boolean playRound(){
            if(gamesToPlay == 0){
                setGamesToPlay(gamesPlayed);
                setGamesPlayed(0);
                calculateResults();
                return false;
            }
            setGamesToPlay(gamesToPlay - 1);
            setGamesPlayed(gamesPlayed + 1);
            setThreshold((mainAgent.getPlayers().size() * mainAgent.parameters.initialBudget) / 2);
            //disaster is pre-calculated at the start of each round
            double f = Math.random();
            this.disasterInThisRound = f <= disasterProbability;
            consoleLog("");
            consoleLog("[ROUND " + gamesPlayed + "] ");
            return true;
        }

        public void checkDisaster(int contributions){
            consoleLog("Players contributed " + contributions + " / " +  gameInfo.threshold);
            if(contributions < gameInfo.threshold) {
                consoleLog("Unreached threshold in round " + gameInfo.gamesPlayed);
                if (!gameInfo.disasterInThisRound) {
                    consoleLog("But the disaster DID NOT happened!");
                } else {
                    consoleLog("And a disaster happened!");
                }
            } else consoleLog("Threshold reached in round " + gameInfo.gamesPlayed + "!");
        }

        public void calculateResults() {
            consoleLog("", true);
            consoleLog("  [ RESULTS ]", true);
            ArrayList<Player> players = mainAgent.getPlayers();
            players.sort(new Comparator<Player>() {

                @Override
                public int compare(Player player, Player t1) {
                    return -(Integer.valueOf(player.accumulatedReminder).compareTo(
                            Integer.valueOf(t1.accumulatedReminder))
                    );
                }
            });

            Iterator<Player> i = players.iterator();
            int lastPlace = 1;
            int lastScore = -1;
            int place = 0;
            while(i.hasNext()){
                Player player = i.next();
                place++;
                int playerPlace = (player.accumulatedReminder == lastScore) ? lastPlace : place;
                lastPlace = playerPlace;
                lastScore = player.accumulatedReminder;
                consoleLog(playerPlace + "- Player " + player.playerID + " with " + player.accumulatedReminder + " points", true);
            }
        }


        JLabel titlelabel;
        JLabel nOfPlayersLabel;
        JLabel roundLabel;
        JLabel gamesPlayedLabel;
        JLabel thresholdLabel;
        JLabel disasterProbabilityLabel;
        JLabel gamesToPlayLabel;

        public JPanel getPanel() {
            return panel;
        }

        JPanel panel;

        GameInfo () {
            this.panel = new JPanel();
            panel.setLayout(new GridLayout(10,1));
            Border raisedbevel = BorderFactory.createRaisedBevelBorder();
            this.titlelabel = new JLabel("Game information");
            this.titlelabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.titlelabel.setBorder(raisedbevel);
            this.nOfPlayersLabel = new JLabel();
            this.roundLabel = new JLabel();
            this.gamesPlayedLabel = new JLabel();
            this.thresholdLabel = new JLabel();
            this.disasterProbabilityLabel = new JLabel();
            this.gamesToPlayLabel = new JLabel();
            this.nOfPlayersLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.roundLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.gamesPlayedLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.thresholdLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.disasterProbabilityLabel.setHorizontalAlignment(SwingConstants.CENTER);;
            this.gamesToPlayLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(this.titlelabel);
            panel.add(this.nOfPlayersLabel);
            panel.add(this.roundLabel);
            panel.add(this.gamesToPlayLabel);
            panel.add(this.gamesPlayedLabel);
            panel.add(this.thresholdLabel);
            panel.add(this.disasterProbabilityLabel);

            this.updateUI();
        }

        public void updateUI(){
            this.nOfPlayersLabel.setText("Number of players: " + this.players);
            this.roundLabel.setText("Current round: " + this.round);
            this.gamesPlayedLabel.setText("Games Played: " + this.gamesPlayed);
            this.thresholdLabel.setText("Disaster threshold: " + this.threshold);
            this.disasterProbabilityLabel.setText("Disaster probability: " + this.disasterProbability);
            this.gamesToPlayLabel.setText("Games to play: " + this.gamesToPlay);
        }
    }

    public class Player {

        JPanel playerPanel;
        final int playerID;
        AID aid;
        int remainigBudget;
        int accumulatedReminder = 0;
        TreeMap<Integer, ArrayList<Integer>> playHistory = new TreeMap<>();
        int currentGame = 0;
        JLabel label1;
        JLabel label2;
        JLabel label3;


        Player (int playerId, int remainigBudget, AID aid) {
            this.playerID = playerId;

            this.remainigBudget = remainigBudget;
            this.aid = aid;

            this.playerPanel = new JPanel();
            Border blackline = BorderFactory.createLineBorder(Color.black);
            Border raisedbevel = BorderFactory.createRaisedBevelBorder();
            playerPanel.setLayout(new GridLayout(3,1,20,20));
            this.label1 = new JLabel("Player " + playerId);
            this.label2 = new JLabel("Remaining budget: " + this.remainigBudget);
            this.label3 = new JLabel("Accumulated reminder: none");
            label1.setHorizontalAlignment(SwingConstants.CENTER);
            label2.setHorizontalAlignment(SwingConstants.CENTER);
            label3.setVerticalAlignment(SwingConstants.CENTER);
            label3.setHorizontalAlignment(SwingConstants.CENTER);
            label3.setVerticalAlignment(SwingConstants.CENTER);
            playerPanel.add(label1);
            label1.setBorder(raisedbevel);
            playerPanel.add(label2);
            playerPanel.add(label3);
            playerPanel.setBorder(blackline);
        }

        public JPanel getPlayerPanel() {
            return this.playerPanel;
        }

        public String contribute(int amount){
            this.remainigBudget = remainigBudget - amount;
            addToHistory(amount);
            updateUI();
            return "[PLAYER ACTION] : Player " + this.playerID + " contributed " + amount + "\n" ;
        }

        public void startGame(int budget) {
            currentGame++;
            this.playHistory.put(currentGame, new ArrayList<>());
            this.remainigBudget = budget;
            updateUI();
        }

        public void addToHistory(int contribution) {
            ArrayList<Integer> play = playHistory.get(currentGame);
            play.add(contribution);
        }

        public int getLastContribution(){
            return playHistory.get(currentGame).get(playHistory.get(currentGame).size() - 1);
        }

        /**
         * Saves the current budget to the score
         */
        public void accumulate(int contributions) {
            if(contributions >= gameInfo.threshold){
                this.accumulatedReminder += this.remainigBudget;
                consoleLog("Player " + this.playerID + " gets " + this.remainigBudget + " points");
            } else if(!gameInfo.disasterInThisRound) {
                this.accumulatedReminder += this.remainigBudget;
                consoleLog("Player " + this.playerID + " gets " + this.remainigBudget + " points");
            }
            updateUI();
        }

        private void updateUI() {
            this.label2.setText("Remaining budget: " + this.remainigBudget);
            this.label3.setText("Acumulated remainder: " + this.accumulatedReminder);
        }
    }

    /**
     * This class produces dialog windows with a text field and two buttons: one to accept and another to cancel.
     *
     * @author  Juan C. Burguillo Rial
     * @version 1.0
     */
    class MyDialog extends JDialog implements ActionListener
    {
        private JTextField oJTF;
        private String dialogName;
        private PSI18_GUI gui;

        /**
         * This is the MyDialog class constructor
         *
         * @param oParent Reference to the object that has created this MyDialog object
         * @param sDialogName Name of this dialog window
         * @param bBool Indicates if this is a modal window (true) or not.
         */
        MyDialog (Frame oParent, String sDialogName, boolean bBool, PSI18_GUI gui) {
            super (oParent, sDialogName, bBool);

            this.gui = gui;
            this.dialogName = sDialogName;

            setBackground (Color.white); // Colors
            setForeground (Color.blue);

            setLayout (new GridLayout(2,1));

            oJTF = new JTextField ("", 30);
            add (oJTF);

            JPanel oJPanel = new JPanel();
            oJPanel.setLayout (new GridLayout(1,2));
            JButton oJBut = new JButton ("OK");
            oJBut.addActionListener (this);
            oJPanel.add (oJBut);
            oJBut  = new JButton ("Cancel");
            oJBut.addActionListener (this);
            oJPanel.add (oJBut);
            add (oJPanel);

            setSize (new Dimension(300,150));
            setLocation (new Point (150, 150));
            setVisible (true);
        }



        /**
         * This method recibes and process events related with this class.
         *
         * @param evt In this parameter we receive the event that has been generated.
         */
        public void actionPerformed (ActionEvent evt) {
            if ("OK".equals (evt.getActionCommand())) {
                String sText = oJTF.getText();
                switch (this.dialogName){
                    case "Disaster probability":
                        float fVal = 0;
                        try {
                            fVal = Float.parseFloat(sText);
                        } catch (Exception e) {
                            dispose();
                        }
                        this.gui.gameInfo.setDisasterProbability(fVal);
                        break;
                    case "Number of games":
                        int iVal = 0;
                        try {
                            iVal = Integer.parseInt (sText);
                        } catch (Exception e) {
                            dispose();
                        }
                        this.gui.gameInfo.setGamesToPlay(iVal);
                        break;
                    default:
                        dispose();
                        break;
                }
                dispose(); // Closing the dialog window
            }

            else if ("Cancel".equals (evt.getActionCommand()))
                dispose();
        }


    } // from MyDialog class
}
