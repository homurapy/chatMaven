package lesson4;

public class CharPrint {
    private char currentLetter = 'A';

    public synchronized void printCharA(){
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'A') {
                        wait();
                    }
                    System.out.print("A");
                    currentLetter = 'B';
                    notify();
                }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
    public synchronized void printCharB(){
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'B') {
                        wait();
                    }
                    System.out.print("B");
                    currentLetter = 'C';
                    notify();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    public synchronized void printCharC(){
            try {
                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'C') {
                        wait();
                    }
                    System.out.print("C");
                    currentLetter = 'A';
                    notify();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




