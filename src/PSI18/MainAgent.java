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
    private GameParametersStruct parameters = new GameParametersStruct();

    @Override
    protected void setup() {
        this.gui = new PSI18_GUI();
        this.addBehaviour(new updateBehaviour(this, 5000));
    }

    public class updateBehaviour extends TickerBehaviour {

        public updateBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            updatePlayers();
            this.getAgent().removeBehaviour(this);
            newGame();
        }
    }

    public int updatePlayers() {
            gui.resetPlayers();
            gui.consoleLog("Updating player list");
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Player");
            template.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(this, template);
                if (result.length > 0) {
                    gui.consoleLog("Found " + result.length + " players");
                }
                for (int i = 0; i < result.length; ++i) {
                    try {
                        //Create and add players to GUI
                        AID name = result[i].getName();
                        PSI18_GUI.Player player = gui.addPlayer(i, 40, name);
                        if(player == null) System.out.print(" NULL PLAYER ");
                        players.add(player);
                    } catch (Exception e) {
                        gui.consoleLog("Can't add new players, maximum reached");
                        e.printStackTrace();
                    }
                }
            } catch (FIPAException fe) {
                gui.consoleLog(fe.getMessage());
            }
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
            for (PSI18_GUI.Player player : players) {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent("Id#" + player.playerID + "#" + parameters.N + "," + parameters.E + "," + parameters.R + "," + parameters.Pd + "," + parameters.numGames);
                msg.addReceiver(player.aid);
                send(msg);
            }
            playGame();
        }

        private void playGame() {
            MainAgent agent = ((MainAgent) getAgent());
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            StringBuilder content = new StringBuilder("NewGame#");
            for (PSI18_GUI.Player player : players) {
                msg.addReceiver(player.aid);
                content.append(player.playerID).append(",");
            }
            content = new StringBuilder(content.substring(0, content.length() - 1));
            msg.setContent(content.toString());
            send(msg);
            //for(int i = 0; i < agent.parameters.R; i++){
                for (PSI18_GUI.Player player : players) {
                    msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.setContent("Action");
                    msg.addReceiver(player.aid);
                    gui.consoleLog("Main Waiting for action of Player " + player.playerID);
                    send(msg);
                    msg = blockingReceive();
                    gui.consoleLog("Main Received " + msg.getContent() + " from " + msg.getSender().getName());
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
                msg.setContent("Results#1#1"); //TODO
                for (PSI18_GUI.Player player : players) {
                    msg.addReceiver(player.aid);
                }
                send(msg);
            //}


            msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent("GameOver"); //TODO
            for (PSI18_GUI.Player player : players) {
                msg.addReceiver(player.aid);
            }
            send(msg);
        }

        @Override
        public boolean done() {
            return false;
        }
    }


    public static class GameParametersStruct {

        int N;
        int E;
        int R;
        float Pd;
        int numGames;
        int currentRound;

        public GameParametersStruct() {
            N = 5;
            E = 4;
            R = 10;
            Pd = 0.2f;
            numGames = 10;
            currentRound = 0;
        }
    }
}
