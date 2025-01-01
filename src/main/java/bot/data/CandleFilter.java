package bot.data;

public class CandleFilter {
    public static long getIntervalDuration(String interval) {
        switch (interval.toLowerCase()) {
            case "1min":
                return 60 * 1000L; // 1 минута
            case "5min":
                return 5 * 60 * 1000L; // 5 минут
            case "15min":
                return 15 * 60 * 1000L; // 15 минут
            case "30min":
                return 30 * 60 * 1000L; // 30 минут
            case "60min":
                return 60 * 60 * 1000L; // 1 час
            case "4hour":
                return 4 * 60 * 60 * 1000L; // 4 часа
            case "1day":
                return 24 * 60 * 60 * 1000L; // 1 день
            default:
                throw new IllegalArgumentException("Unknown interval: " + interval);
        }
    }
}
