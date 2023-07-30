package org.vkartashov.hla9.util;

public class Constants {

    /**
     * Dates in script are generated between 1950-01-01 and 2001-12-31
     * Since there is no median function in MySQL that could be reused here
     * using static values
     */
    public static final String ONE_PERCENT_DATE = "1950-01-30";
    public static final String FIFTY_PERCENT_DATE = "1975-12-31";
    public static final String GENERATED_DATES_START_RANGE = "1950-01-01";
    public static final String GENERATED_DATES_END_RANGE = "2001-12-31";
    public static final String INSERTS_ROUTINE_DUMMY_USER_FIRST_NAME = "DUMMY_NAME";

}
