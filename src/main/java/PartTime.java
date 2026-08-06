public class PartTime {
    private int probability;
    private String firstMessage;
    private String secondMessage;
    private String runMessage;
    private String endMessage;
    private int moneyReward;
    private int popularityReward;
    private String logMessage;

    PartTime(int probability,String firstMessage,String secondMessage,String runMessage,String endMessage,int moneyReward,int popularityReward,String logMessage){
        this.probability = probability;
        this.firstMessage = firstMessage;
        this.secondMessage = secondMessage;
        this.runMessage = runMessage;
        this.endMessage = endMessage;
        this.moneyReward = moneyReward;
        this.popularityReward = popularityReward;
        this.logMessage = logMessage;
    }


        public int getProbability() {
        return probability;
    }

    public String getFirstMessage() {
        return firstMessage;
    }

    public String getSecondMessage() {
        return secondMessage;
    }

    public String getRunMessage() {
        return runMessage;
    }

    public String getEndMessage() {
        return endMessage;
    }

    public int getMoneyReward() {
        return moneyReward;
    }

    public int getPopularityReward() {
        return popularityReward;
    }

    public String getLogMessage() {
        return logMessage;
    }

}
