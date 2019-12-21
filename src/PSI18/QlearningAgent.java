package PSI18;

import java.util.ArrayList;
import java.util.Random;

/**
 * Q Learning Agent
 *
 * Based on tutorial and code provided in:
 * http://technobium.com/reinforcement-learning-q-learning-java/
 *
 */

public class QlearningAgent extends BaseAgent {
    private final double alpha = 0.1; // Learning rate
    private final double gamma = 0.9; // Eagerness - 0 looks in the near future, 1 looks in the distant future

    private final int statesCount = getParameters().R;
    private final int[] possibleActions = {0,1,2,3,4};
    private final int outcomesCount = possibleActions.length;

    private final double reward = 100;
    private double[][] Q;    // Q learning

    int accumulated = 0;
    ArrayList<Integer> contributions = new ArrayList<>();
    int gamesPlayed = 0;

    @Override
    public void onConfigured() {
        init();
    }

    @Override
    public int playRound(int round) {
        Random random = new Random();
        int contributed;
        int currentState = contributions.size();
        contributed = (getExplotationIndex() < Math.random()) ?
                (random.nextInt(5)) :
                getPolicyFromState(currentState);
        contributions.add(contributed);
        return contributed;
    }

    @Override
    public void onResult(String result) {
        String[] results = result.split("#")[1].split(",");
        for(int i = 0; i < results.length; i++){
            accumulated += Integer.valueOf(results[i]);
        }
    }

    @Override
    public void onRoundFinished() {
        calculateQ();
        accumulated = 0;
        gamesPlayed++;
        contributions.clear();
        printQ();
    }


    public void init() {
        Q = new double[statesCount][outcomesCount];
        initializeQ();
    }
    
    //Set Q values to 0
    void initializeQ()
    {
        for (int i = 0; i < statesCount; i++){
            for(int j = 0; j < outcomesCount; j++){
                Q[i][j] = 0;
            }
        }
    }

    void calculateQ() {
        int state = 0;
        for (Integer contribution : contributions) {
            int leftForThreshhold = ((this.getParameters().N * this.getParameters().E) / 2) - accumulated;
            double thresholdFactor = (leftForThreshhold <= 0) ? 1 : 0.2;
            double contributionFactor = ((getParameters().E - contribution)/getParameters().E);
            
            double q = Q[state][contribution];
            double maxQ = maxQ(state);
            int r = (int)(reward * thresholdFactor * contributionFactor);
            // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
            double value = q + alpha * (r + gamma * maxQ - q);
            Q[state][contribution] = value;
            state++;
        }
    }


    double maxQ(int state) {
        int[] actionsFromState = possibleActions;
        //the learning rate and eagerness will keep the W value above the lowest reward
        double maxValue = -10;
        for (int action : actionsFromState) {
            double value = Q[state][action];
            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }

    void printPolicy() {
        System.out.println("\nPrint policy");
        for (int i = 0; i < statesCount; i++) {
            System.out.println("From state " + i + " goto state " + getPolicyFromState(i));
        }
    }

    int getPolicyFromState(int state) {
        int[] actionsFromState = possibleActions;

        double maxValue = Double.MIN_VALUE;
        int policyGotoState = state;

        // Pick to move to the state that has the maximum Q value
        for (int nextState : actionsFromState) {
            double value = Q[state][nextState];

            if (value > maxValue) {
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }

    void printQ() {
        System.out.println("Q matrix");
        for (int i = 0; i < Q.length; i++) {
            System.out.print("From state " + i + ":  ");
            for (int j = 0; j < Q[i].length; j++) {
                System.out.printf("%6.2f ", (Q[i][j]));
            }
            System.out.println();
        }
    }

    private double getExplotationIndex(){
        if(gamesPlayed <= 100) return 0; //Allways explorate

        else if(100 < gamesPlayed && gamesPlayed < 300) {
            return (gamesPlayed - 100) / 220; // increase from 0 to 0.9
        }
        else return 0.9;
    }

}
