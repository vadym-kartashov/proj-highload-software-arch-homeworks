package org.vkartashov.collections;

public class SortingUtil {

        public static int[]  countingSort(int[] arr) {
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;

            // Find minimum and maximum values
            for (int num : arr) {
                if (num > max) {
                    max = num;
                }
                if (num < min) {
                    min = num;
                }
            }

            int[] countArray = new int[max - min + 1];

            // Count occurrences of each number
            for (int num : arr) {
                countArray[num - min]++;
            }

            int j = 0;
            for (int i = 0; i < countArray.length; i++) {
                while (countArray[i] > 0) {
                    arr[j++] = i + min;
                    countArray[i]--;
                }
            }
            return arr;
        }

}

