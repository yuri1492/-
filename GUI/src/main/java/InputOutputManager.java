import java.util.Scanner;

public class InputOutputManager {
    public static Scanner scanner = new Scanner(System.in);
    public static int inputNumber(int min, int max) {
        while (true) {
            System.out.print(min + "～" + max + "を入力してください: ");

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                System.out.println();

                if (value >= min && value <= max) {
                    return value;
                }
            } else {
                System.out.println();
                scanner.next(); // 不正入力を読み飛ばす
                scanner.nextLine();
            }

            InputOutputManager.coloerPrintl("入力が正しくありません。\n", Color.YELLOW);
        }
    }
    public static void coloerPrintl(String string,Color color) {
        System.out.println(color.getCode() + string + Color.RESET.getCode());
    }
    public static void coloerSet(Color color) {
        System.out.printf(color.getCode());
    }
    public static void colorReset(){
        System.out.printf(Color.RESET.getCode());
    }
    public static void wait(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static int repeatNum(int max,int length){
        return (max - length) * 2 + 2;
    }
}
