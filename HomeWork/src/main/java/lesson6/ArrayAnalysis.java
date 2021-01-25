package lesson6;

import java.util.Arrays;

public class ArrayAnalysis {
    public static void main (String[] args) {
        System.out.println(arrayAvailableOneFour(new int[]{7, 2, 3, 3, 5, 2, 7, 8}));
    }

    private ArrayAnalysis() {};
    public static int[] arrayAfterLast4 (int[] arrays) {
        if(arrays != null) {
            for (int i = (arrays.length - 2); i >= 0; i--) {
                if (arrays[i] == 4) {
                    int[] ints = Arrays.copyOfRange(arrays, i+1, arrays.length);
                    return ints;
                }
            }
        }
        throw new RuntimeException("missing element with value 4");
    }

    public static boolean arrayAvailableOneFour (int[] arrays) {
        if(arrays != null) {
            for (int i = arrays.length - 1; i > 0; i--) {
                if (arrays[i] == 4 || arrays[i] == 1) {
                    return true;
                }
            }
        }
        return false;
    }
}
