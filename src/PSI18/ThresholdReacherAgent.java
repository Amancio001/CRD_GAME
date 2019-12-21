package PSI18;

public class ThresholdReacherAgent extends BaseAgent {

    /**
     * This agent plays 2 until the threshold is reached
     */

    int accumulated = 0;

    public int playRound(int round) {
        int leftForThreshhold = ((this.getParameters().N * this.getParameters().E) / 2) - accumulated;
        return (leftForThreshhold > 0) ? 2 : 0;
    }

    @Override
    public void onRoundFinished() {
        accumulated = 0;
    }

    @Override
    public void onResult(String result) {
        String[] results = result.split("#")[1].split(",");
        for(int i = 0; i < results.length; i++){
            accumulated += Integer.valueOf(results[i]);
        }
    }
}
