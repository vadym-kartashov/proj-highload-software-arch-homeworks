package org.vkartashov.hla12;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class RandomUtil {

    private static Random randomizer = new Random();

    public static String generateRandomString(int length) {
        // Define the characters to be used
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder result = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }

    public static String generateRandomString(String prefix, int length) {
        return prefix + generateRandomString(length);
    }

    public static Map<String, String> generateRandomOrderedHashTable(int size, int valueLength) {
        Map<String, String> result = new LinkedHashMap<>(size);
        for (int i = 0; i < size; i++) {
            result.put(Integer.toString(i), generateRandomString(valueLength));
        }
        return result;
    };

}
