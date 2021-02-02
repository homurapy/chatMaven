import lesson6.ArrayAnalysis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ArrayAnalysisTest {
    @ParameterizedTest
    @MethodSource("ParametersProvider")
    void arrayAfterLast4basaTest (int[] expected, int[] a) {
        Assertions.assertArrayEquals(expected, ArrayAnalysis.arrayAfterLast4(a));
    }

    private static Stream<Arguments> ParametersProvider () {
        return Stream.of(
                Arguments.arguments(new int[]{5, 6, 7, 8}, new int[]{1, 2, 3, 4, 5, 6, 7, 8}),
                Arguments.arguments(new int[]{7, 8}, new int[]{1, 2, 3, 4, 5, 4, 7, 8}),
                Arguments.arguments(new int[]{7}, new int[]{4, 7})
        );
    }

    @Test
    void arrayAfterLast4testException () {
        Assertions.assertThrows(RuntimeException.class, () -> ArrayAnalysis.arrayAfterLast4(new int[]{1, 2, 3}));
        Assertions.assertThrows(RuntimeException.class, () -> ArrayAnalysis.arrayAfterLast4(new int[]{}));
    }

    @ParameterizedTest
    @MethodSource("ParametersProvider2")
    void arrayAvailableOneFourbasaTestFalse (boolean expected, int[] a) {
        Assertions.assertFalse(expected, String.valueOf(ArrayAnalysis.arrayAvailableOneFour(a)));
    }
    private static Stream<Arguments> ParametersProvider2 () {
        return Stream.of(
//                Arguments.arguments(true, new int[]{1, 2, 3, 4, 5, 6, 7, 8}),
                Arguments.arguments(false, new int[]{7, 2, 3, 3, 5, 2, 7, 8}),
//                Arguments.arguments(true, new int[]{4, 7}),
//                Arguments.arguments(true, new int[]{1, 7}),
                Arguments.arguments(false, new int[]{3, 7})
        );
    }
    @ParameterizedTest
    @MethodSource("ParametersProvider3")
    void arrayAvailableOneFourbasaTestTrue (boolean expected, int[] a) {
        Assertions.assertTrue(expected, String.valueOf(ArrayAnalysis.arrayAvailableOneFour(a)));
    }
    private static Stream<Arguments> ParametersProvider3 () {
        return Stream.of(
                Arguments.arguments(true, new int[]{1, 2, 3, 4, 5, 6, 7, 8}),
//                Arguments.arguments(false, new int[]{7, 2, 3, 3, 5, 2, 7, 8}),
                Arguments.arguments(true, new int[]{4, 7}),
                Arguments.arguments(true, new int[]{1, 7})
//                Arguments.arguments(false, new int[]{3, 7})
        );
    }

}

