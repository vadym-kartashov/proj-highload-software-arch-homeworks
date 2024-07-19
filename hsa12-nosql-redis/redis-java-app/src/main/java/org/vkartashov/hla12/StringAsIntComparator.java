package org.vkartashov.hla12;

import java.util.Comparator;

public class StringAsIntComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        int intO1 = Integer.parseInt(o1);
        int intO2 = Integer.parseInt(o2);

        return Integer.compare(intO1, intO2);
    }

    public static Comparator<String> COMPARATOR = new StringAsIntComparator();

}
