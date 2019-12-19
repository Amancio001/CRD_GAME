package PSI18;

import jdk.jfr.Event;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class PSI18_GUI extends JFrame implements ActionListener {

    private JLabel label;
    JTextArea textArea;
    private boolean verbose = true;
    public GameInfo gameInfo;

    PSI18_GUI() {
        super("Collective-Risk Dilemma Game (PSI 18)");
        this.setBackground(Color.lightGray);
        this.setForeground(Color.black);

        JMenu editMenu = new JMenu( "Edit");
            JMenuItem itemResetPlayers = new JMenuItem("Reset Players");
            JMenuItem itemRemPlayer    = new JMenu("Remove player");
            editMenu.add(itemResetPlayers);
            editMenu.add(itemRemPlayer);

            for(int i = 1; i <=7; i++){
                JMenuItem itemPlayer = new JMenuItem("Remove Player " + i);
                itemRemPlayer.add(itemPlayer);
                itemPlayer.addActionListener(this);
            }
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
        JPanel players = new JPanel();
        players.setLayout(new GridLayout(5,1, 5,5));
        Player player1 = new Player(1, 40);
        Player player2 = new Player(2, 40);
        Player player3 = new Player(3, 40);
        Player player4 = new Player(4, 40);
        Player player5 = new Player(5, 40);
        players.add(player1.getPlayerPanel(),0,0);
        players.add(player2.getPlayerPanel(),0,1);
        players.add(player3.getPlayerPanel(),0,2);
        players.add(player4.getPlayerPanel(),0,3);
        players.add(player5.getPlayerPanel(),0,4);
        this.add(players);
        GameInfo gameInfoFrame = new GameInfo();
        this.gameInfo = gameInfoFrame;
        this.add(gameInfoFrame.getPanel());
        this.textArea = new JTextArea(1, 1);
        JScrollPane scroll = new JScrollPane (textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroll);
        scroll.setAutoscrolls(true);
        scroll.setFocusCycleRoot(true);
        textArea.setEditable(false);
        textArea.setFocusCycleRoot(true);
        textArea.setAutoscrolls(true);
        this.setSize (new Dimension(1000,800));     // Window size
        this.setLocation (new Point (100, 100));
        this.setVisible(true);
        player1.contribute(1);
        return;
    }

    public void actionPerformed (ActionEvent evt) {
        String event = evt.getActionCommand();
        if(event.contains("Verbose")) {
            this.verbose = !this.verbose;
            if (!this.verbose) {
                this.textArea.append("[SYSTEM]: Verbose Off, I'll stay quiet\n");
            } else {
                this.textArea.append("[SYSTEM]: Verbose On!\n");
            }
            return;
        }
        if(!this.verbose) return;
        this.textArea.append("[USER ACTION]: Selected *" + event + "* option\n");
        if(event.contains("Remove Player")){
            this.textArea.append("[SYSTEM]: But removing players is not implemented\n");
            this.textArea.append("[" + event.split(" ")[1] + " " + event.split(" ")[2] + "]: I'm indestructible!\n");
            this.textArea.append("[SYSTEM]: Just for now...\n");
            return;
        }
        switch(event) {
            case "Number of games":
                new MyDialog(this, "Number of games", true, this);
                break;
            case "Disaster probability":
                new MyDialog(this, "Disaster probability", true, this);
                break;
            case "About":
                this.textArea.append("\nCollective-Risk Dilemma Game (PSI 18)\n\n");
                this.textArea.append("Author: Amancio Pontes Hermo\n");
                this.textArea.append("Version: 0.1 (interface only)\n");
                this.textArea.append("Contact: amancio001@gmail.com\n\n");
        }
    }

    public static void main (String[] args) {
        PSI18_GUI gui = new PSI18_GUI();
    }

    public class GameInfo {

        int players = 7;
        int round = 0;
        int gamesPlayed = 0;
        int gamesToPlay = 0;
        float threshold = 0;
        float disasterProbability = 0.8f;
        float uncertaintyParameter = 0.25f;

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

        public void setThreshold(float threshold) {
            this.threshold = threshold;
            this.updateUI();
        }

        public void setDisasterProbability(float disasterProbability) {
            this.disasterProbability = disasterProbability;
            this.updateUI();
        }

        public void setUncertaintyParameter(float uncertaintyParameter) {
            this.uncertaintyParameter = uncertaintyParameter;
            this.updateUI();
        }


        JLabel titlelabel;
        JLabel nOfPlayersLabel;
        JLabel roundLabel;
        JLabel gamesPlayedLabel;
        JLabel thresholdLabel;
        JLabel disasterProbabilityLabel;
        JLabel uncertaintyParameterLabel;
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
            this.uncertaintyParameterLabel = new JLabel();
            this.gamesToPlayLabel = new JLabel();
            this.nOfPlayersLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.roundLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.gamesPlayedLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.thresholdLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.disasterProbabilityLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.uncertaintyParameterLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.gamesToPlayLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(this.titlelabel);
            panel.add(this.nOfPlayersLabel);
            panel.add(this.roundLabel);
            panel.add(this.gamesToPlayLabel);
            panel.add(this.gamesPlayedLabel);
            panel.add(this.thresholdLabel);
            panel.add(this.disasterProbabilityLabel);
            panel.add(this.uncertaintyParameterLabel);

            this.updateUI();
        }

        public void updateUI(){
            this.nOfPlayersLabel.setText("Number of players: " + this.players);
            this.roundLabel.setText("Current round: " + this.round);
            this.gamesPlayedLabel.setText("Games Played: " + this.gamesPlayed);
            this.thresholdLabel.setText("Disaster threshold: " + this.threshold);
            this.disasterProbabilityLabel.setText("Disaster probability: " + this.disasterProbability);
            this.uncertaintyParameterLabel.setText("Uncertainty parameter: " + this.uncertaintyParameter);
            this.gamesToPlayLabel.setText("Games to play: " + this.gamesToPlay);
        }
    }
    public class Player {
        JPanel playerPanel;
        final int playerID;
        int remainigBudget;
        JLabel label1;
        JLabel label2;
        JLabel label3;


        Player (int playerId, int remainigBudget) {
            this.playerID = playerId;
            this.remainigBudget = remainigBudget;

            this.playerPanel = new JPanel();
            Border blackline = BorderFactory.createLineBorder(Color.black);
            Border raisedbevel = BorderFactory.createRaisedBevelBorder();
            playerPanel.setLayout(new GridLayout(3,1,20,20));
            this.label1 = new JLabel("Player " + playerId);
            this.label2 = new JLabel("Remaining budget: " + this.remainigBudget);
            this.label3 = new JLabel("Last contribution: none");
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

            this.label2.setText("Remaining budget: " + this.remainigBudget);
            this.label3.setText("Last contribution: " + amount);
            return "[PLAYER ACTION] : Player " + this.playerID + " contributed " + amount + "\n" ;
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
