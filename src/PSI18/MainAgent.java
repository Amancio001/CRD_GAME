package PSI18;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import PSI18.PSI18_GUI;

public class MainAgent extends Agent {

    private PSI18_GUI gui;

    private AID aid = new AID("MainAgent", AID.ISLOCALNAME);

    static final int FIRST_ROUND = 0;
    static final int LAST_ROUND = 10;
    private boolean gameRunning = false;
    private int currentRound = FIRST_ROUND;

    protected void setup() {
        this.gui = new PSI18_GUI();
        /*Collect agents*/
        System.out.println("Main Agent " + getAID().getName() + " is ready.");
    }

    public void startGame() {
        this.addBehaviour(new StartGameBehaviour());
    }

    public class StartGameBehaviour extends Behaviour {

        public void action() {
            gameRunning = true;
            switch (currentRound) {
                case (FIRST_ROUND):
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    // TODO: foreach player in players
                    msg.addReceiver();
                    msg.setLanguage("English");
                    msg.setContent("New game");
                    send(msg);
                    break;
                case (2):
                    break;
                case (3):
                    break;
                case (LAST_ROUND):
                    gameRunning = false;
                    break;
            }
        }

        @Override
        public boolean done() {
            return currentRound == LAST_ROUND;
        }
    }

    public class DiscoveryBehaviour extends OneShotBehaviour{

        @Override
        public void action() {

        }
    }

    public class RoundBehaviour extends OneShotBehaviour {
        @Override
        public void action() {

        }
    }


}
