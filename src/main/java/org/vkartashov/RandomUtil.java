package org.vkartashov;

import java.util.Random;

public class RandomUtil {

    public static int[] generateRandomIntArray(int num, boolean negative, int inRange, long seed) {
        Random rd = new Random(seed); // creating Random object
        int[] arr = new int[num];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rd.nextBoolean() && negative ? -rd.nextInt(inRange) : rd.nextInt(inRange); // storing random integers in an array
        }
        return arr;
    }

    public static int[] generateRandomIntArray(int num, int inRange, long seed) {
        return generateRandomIntArray(num, false, inRange, seed);
    }

}
