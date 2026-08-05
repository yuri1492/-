public class Promotion {
    private String choiceMessage;
    private String viewMessage;
    private int pop;
    private int cost;
    private boolean check;

    Promotion(String choiceMessage,String viewMessage,int cost,int pop){
        this.choiceMessage = choiceMessage;
        this.viewMessage = viewMessage;
        this.cost = cost;
        this.pop = pop;
        this.check = false;
    }

    public String getChoiceMessage(){
        return choiceMessage;
    }
    public String getViewMessage(){
        return viewMessage;
    }
    public int getCost(){
        return cost;
    }
    public int getPop(){
        return pop;
    }
    public boolean getCheck(){
        return check;
    }
    public void setCheck(boolean check){
        this.check = check;
    }
}
