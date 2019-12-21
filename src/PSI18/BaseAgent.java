package PSI18;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public abstract class BaseAgent extends Agent {

    private State state;
    private AID mainAgent;
    private int myId;
    private int[] opponentsIds;
    private ACLMessage msg;
    private MainAgent.GameParametersStruct parameters = new MainAgent.GameParametersStruct();

    protected void setup() {
        state = State.s0_Configuring;

        //Register in the yellow pages as a player
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Player");
        sd.setName("Game");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new Play());
        //Set the agent name
        System.out.println("Agent" + " " + getAID().getName() + " is ready.");

    }

    protected void takeDown() {
        //Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println("RandomPlayer " + getAID().getName() + " terminating.");
    }

    private enum State {
        s0_Configuring, s1_AwaitingGame, s2_InRound, s3_AwaitingResult
    }

    private class Play extends CyclicBehaviour {

        @Override
        public void action() {
            BaseAgent agent = ((BaseAgent) this.getAgent());
            System.out.println(getAID().getName() + ":" + state.name());
            msg = receive();
            if (msg != null) {
                System.out.println(getAID().getName() + " received " + msg.getContent() + " from " + msg.getSender().getName()); //DELETEME
                // To be able to reset the player at any state
                if(msg.getContent().startsWith("Id#")) state = State.s0_Configuring;
                switch (state) {
                    case s0_Configuring:
                        //If INFORM Id#_#_,_,_,_ PROCESS SETUP --> go to state 1
                        //Else ERROR
                        if (msg.getContent().startsWith("Id#") && msg.getPerformative() == ACLMessage.INFORM) {
                            boolean parametersUpdated = false;
                            try {
                                parametersUpdated = parseSetupMessage(msg);
                                agent.mainAgent = msg.getSender();
                            } catch (NumberFormatException e) {
                                System.out.println(getAID().getName() + ":" + state.name() + " - Bad message");
                            }
                            if (parametersUpdated) state = State.s1_AwaitingGame;

                        } else {
                            System.out.println(getAID().getName() + ":" + state.name() + " - Unexpected message");
                        }
                        break;
                    case s1_AwaitingGame:
                        boolean parametersUpdated = false;
                        if (msg.getPerformative() == ACLMessage.INFORM) {
                            if (msg.getContent().startsWith("Id#")) { //Game settings updated
                                try {
                                    parseSetupMessage(msg);
                                    parametersUpdated = true;
                                } catch (NumberFormatException e) {
                                    System.out.println(getAID().getName() + ":" + state.name() + " - Bad message");
                                }
                            } else if (msg.getContent().startsWith("NewGame")) {
                                state = State.s2_InRound;
                            }
                        } else {
                            System.out.println(getAID().getName() + ":" + state.name() + " - Unexpected message");
                        }
                        break;
                    case s2_InRound:
                        if (msg.getPerformative() == ACLMessage.REQUEST && msg.getContent().startsWith("Action")) {
                            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                            msg.addReceiver(mainAgent);
                            agent.parameters.currentRound = agent.parameters.currentRound + 1;
                            msg.setContent("Action#" + playRound(agent.parameters.currentRound));
                            System.out.println(getAID().getName() + " sent " + msg.getContent());
                            send(msg);
                            state = State.s3_AwaitingResult;
                        } else if(msg.getContent().startsWith("GameOver")){
                            state = State.s1_AwaitingGame;
                        } else {
                            System.out.println(getAID().getName() + ":" + state.name() + " - Unexpected message:" + msg.getContent());
                        }
                        break;
                    case s3_AwaitingResult:
                        if (msg.getPerformative() == ACLMessage.INFORM && msg.getContent().startsWith("Results#")) {
                            System.out.println(getAID().getName() + ":" + state.name() + " " +  msg.getContent());
                            state = State.s2_InRound;
                        } else {
                            System.out.println(getAID().getName() + ":" + state.name() + " - Unexpected message");
                        }
                        break;
                }
            } else block();
        }

        /**
         * Validates and extracts the parameters from the setup message
         *
         * @param msg ACLMessage to process
         * @return true on success, false on failure
         */
        private boolean parseSetupMessage(ACLMessage msg) throws NumberFormatException {
            BaseAgent agent = ((BaseAgent) this.getAgent());
            String[] parts = msg.getContent().split("#");
            agent.myId = Integer.parseInt(parts[1]);
            parts = parts[2].split(",");
            if (parts.length != 5) throw new NumberFormatException();
            for (int i = 0; i < parts.length; i++) {
                switch (i) {
                    case 0:
                        agent.parameters.N = Integer.parseInt(parts[0]);
                        break;
                    case 1:
                        agent.parameters.E = Integer.parseInt(parts[1]);
                        break;
                    case 2:
                        agent.parameters.R = Integer.parseInt(parts[2]);
                        break;
                    case 3:
                        agent.parameters.Pd = Float.parseFloat(parts[3]);
                        break;
                    case 4:
                        agent.parameters.numGames = Integer.parseInt(parts[4]);
                        break;
                }
            }
            return true;
        }
    }

    /**
     * Agents need to implement this method in order to make decisions along the rounds
      * @return An integer with the desired decision (between 0 and 4)
     */
    public abstract int playRound(int round);
}