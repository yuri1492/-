public enum Mode{
    NORMAL("ノーマル"),
    ENDLESS("エンドレス");


    private String name;
    Mode(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}