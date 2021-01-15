import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main (String[] args) {
        CharPrint cP = new CharPrint();
        ExecutorService service = Executors.newFixedThreadPool(3);
        service.execute(new Thread(() -> {cP.printCharA();}));
        service.execute(new Thread(() -> {cP.printCharB();}));
        service.execute(new Thread(() -> {cP.printCharC();}));
        }

}
