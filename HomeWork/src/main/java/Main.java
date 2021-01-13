public class Main {
    public static void main (String[] args) {
        CharPrint cP = new CharPrint();
        Thread t1 = new Thread(() -> {
            cP.printCharA();
        });
        Thread t2 = new Thread(() -> {
            cP.printCharB();
        });
        Thread t3 = new Thread(() -> {
            cP.printCharC();
        });
        t1.start();
        t2.start();
        t3.start();

    }
}
