
public class CharPrint {
    private final Object mon = new Object();
    private char currentLetter = 'A';

    public void printCharA(){
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'A') {
                        mon.wait();
                    }
                    System.out.print("A");
                    currentLetter = 'B';
                    mon.notify();

                }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    public void printCharB(){
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'B') {
                        mon.wait();
                    }
                    System.out.print("B");
                    currentLetter = 'C';
                    mon.notify();

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void printCharC(){
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'C') {
                        mon.wait();
                    }
                    System.out.print("C");
                    currentLetter = 'A';
                    mon.notify();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    }



