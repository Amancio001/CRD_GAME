package PSI18;

import java.util.Random;

public class RandomAgent extends BaseAgent {

    public RandomAgent() {
        super();
    }

    @Override
    public int playRound() {
        Random random = new Random();
        return (random.nextInt(3) + 1);
    }
}
