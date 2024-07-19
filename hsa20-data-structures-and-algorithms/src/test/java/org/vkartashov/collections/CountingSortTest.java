package org.vkartashov.collections;

import org.junit.Test;
import org.vkartashov.PerformanceMeasurementUtil;
import org.vkartashov.RandomUtil;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
public class CountingSortTest {

    @Test
    public void emptyArray() {
        int[] array = {};
        int[] expectedArray = {};

        int[] sortedArray = SortingUtil.countingSort(array);

        assertArrayEquals(expectedArray, sortedArray);
    }

    @Test
    public void sortedArray() {
        int[] array = {1, 2, 3, 4, 5};
        int[] expectedArray = {1, 2, 3, 4, 5};

        int[] sortedArray = SortingUtil.countingSort(array);

        assertArrayEquals(expectedArray, sortedArray);
    }

    @Test
    public void unsortedArray() {
        int[] array = {5, 3, 2, 1, 4};
        int[] expectedArray = {1, 2, 3, 4, 5};

        int[] sortedArray = SortingUtil.countingSort(array);

        assertArrayEquals(expectedArray, sortedArray);
    }

    @Test
    public void arrayWithDuplicates() {
        int[] array = {1, 2, 3, 4, 5, 1, 2, 3};
        int[] expectedArray = {1, 1, 2, 2, 3, 3, 4, 5};

        int[] sortedArray = SortingUtil.countingSort(array);

        assertArrayEquals(expectedArray, sortedArray);
    }

    public static void main(String[] args) {
        String filename = "countingSort.csv";
        for (int i = 50; i < 10000; i+=50) {
            int[] randomBestCaseArray = RandomUtil.generateRandomIntArray(i, i, 1L);
            PerformanceMeasurementUtil.measureComplexity("BestCase", randomBestCaseArray.length, filename, () -> SortingUtil.countingSort(randomBestCaseArray));
            int[] randomWorstCaseArray = RandomUtil.generateRandomIntArray(i, i*1000, 1L);
            PerformanceMeasurementUtil.measureComplexity("WorstCase", randomBestCaseArray.length, filename, () -> SortingUtil.countingSort(randomWorstCaseArray));
        }
    }

    private static void measureSortTime(int[] array) {
        long startTime = System.nanoTime();
        SortingUtil.countingSort(array);
        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime - startTime) + " nanoseconds");
    }

}
