package uz.pdp.utils;

public class TimeFormatter {

    /**
     * Converts seconds into a formatted string "minutes:seconds".
     *
     * @param totalSeconds The total number of seconds to convert.
     * @return A string representing the time in "minutes:seconds" format.
     */
    public static String formatSecondsToMinutes(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }


}
