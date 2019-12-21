package PSI18;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class MainAgent extends Agent {

    private PSI18_GUI gui;
    private ArrayList<PSI18_GUI.Player> players = new ArrayList<>();
    public GameParametersStruct parameters = new GameParametersStruct();

    public ArrayList<PSI18_GUI.Player> getPlayers() {
        return players;
    }

    @Override
    protected void setup() {
        MainAgent agent = this;
        Runnable guiThread = () -> {
            agent.setGui(new PSI18_GUI(agent));
            updatePlayers();
        };
        guiThread.run();
    }

    public void setGui(PSI18_GUI gui) {
        this.gui = gui;
    }

    public synchronized void removePlayer(PSI18_GUI.Player player){
        players.remove(player);
    }

    public int updatePlayers() {
            System.out.println(gui);
            gui.resetPlayers();
            players.clear();
            System.out.println("Updating player list...");
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Player");
            template.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(this, template);
                if (result.length > 0) {
                    System.out.println("Found " + result.length + " players");
                }
                for (int i = 0; i < result.length; ++i) {
                    try {
                        //Create and add players to GUI
                        AID name = result[i].getName();
                        PSI18_GUI.Player player = gui.addPlayer(i, 40, name);
                        if(player == null) System.out.print(" NULL PLAYER ");
                        players.add(player);
                    } catch (Exception e) {
                        System.out.println("Can't add new players, maximum reached");
                        e.printStackTrace();
                    }
                }
            } catch (FIPAException fe) {
                System.out.println(fe.getMessage());
            }
            gui.gameInfo.setPlayers(players.size());
            gui.updatePlayerRemover();
            gui.revalidate();
            return 0;
    }

    public void newGame(){
        GameBehaviour gameBehaviour = new GameBehaviour();
        this.addBehaviour(gameBehaviour);
    }



    /**
     * In this behavior this agent manages the course of a match during all the
     * rounds.
     */
    private class GameBehaviour extends SimpleBehaviour {

        @Override
        public void action() {
            parameters.N = players.size();
            parameters.Pd = gui.gameInfo.disasterProbability;
            parameters.numGames = gui.gameInfo.gamesToPlay;
            for (PSI18_GUI.Player player : players) {
                player.accumulatedReminder = 0;
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent("Id#" + player.playerID + "#" + parameters.N + "," + parameters.E + "," + parameters.R + "," + parameters.Pd + "," + parameters.numGames);
                msg.addReceiver(player.aid);
                send(msg);
            }
            playGame();
        }

        private void playGame() {
            while(gui.gameInfo.playRound()) {
            MainAgent agent = ((MainAgent) getAgent());
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            StringBuilder content = new StringBuilder("NewGame#");
            for (PSI18_GUI.Player player : players) {
                msg.addReceiver(player.aid);
                content.append(player.playerID).append(",");
                player.startGame(parameters.initialBudget);
            }
            content = new StringBuilder(content.substring(0, content.length() - 1));
            msg.setContent(content.toString());
            send(msg);
            int contributions = 0;
                for (int i = 0; i < agent.parameters.R; i++) {
                    for (PSI18_GUI.Player player : players) {
                        msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.setContent("Action");
                        msg.addReceiver(player.aid);
                        System.out.println("Main Waiting for action of Player " + player.playerID);
                        send(msg);
                        msg = blockingReceive();
                        int playerContribution = Integer.parseInt(msg.getContent().split("#")[1]);
                        contributions += playerContribution;
                        player.contribute(playerContribution);
                        System.out.println("Main Received " + msg.getContent() + " from " + msg.getSender().getName());
                        //                WakerBehaviour wake = new WakerBehaviour(this.getAgent(), 10000){ TODO
                        //                    @Override
                        //                    protected void onWake() {
                        //                        super.onWake();
                        //                        System.out.println("Timedout!");
                        //                    }
                        //                };
                        //                class ReceiveBehaviour extends OneShotBehaviour {
                        //                    @Override
                        //                    public void action() {
                        //                        ACLMessage response = receive();
                        //                        if(response != null){
                        //
                        //                        } else block();
                        //                    }
                        //                }
                    }
                    msg = new ACLMessage(ACLMessage.INFORM);
                    content = new StringBuilder("Results#");
                    for (PSI18_GUI.Player player : players) {
                        msg.addReceiver(player.aid);
                        content.append(player.getLastContribution()).append(",");
                    }
                    content = new StringBuilder(content.substring(0, content.length() - 1));
                    msg.setContent(content.toString());
                    send(msg);
                }


            msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent("GameOver"); //TODO
            gui.gameInfo.checkDisaster(contributions);
            for (PSI18_GUI.Player player : players) {
                msg.addReceiver(player.aid);
                player.accumulate(contributions);
            }
            send(msg);
            stop();
            }
        }

        private void stop(){
            while(gui.stop){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean done() {
            return true;
        }
    }


    public static class GameParametersStruct {

        int N;
        int E;
        int R;
        float Pd;
        int numGames;
        int currentRound;
        int initialBudget = 40;

        public GameParametersStruct() {
            N = 5;
            E = initialBudget;
            R = 10;
            Pd = 0.2f;
            numGames = 10;
            currentRound = 0;
        }
    }
}
