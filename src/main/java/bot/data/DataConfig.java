package bot.data;

import java.util.Arrays;
import java.util.List;

public class DataConfig {

    private static int AmountOfCryptocurrency;
    private static int CandlesAmount;
    private static List<String> timeframes;
    private static int macdShortPeriod;
    private static int macdLongPeriod;
    private static int macdSignalPeriod;

    public DataConfig() {
        timeframes = Arrays.asList("5min","4hour");
        AmountOfCryptocurrency = 2;
        CandlesAmount = 40;
        macdShortPeriod = 12;
        macdLongPeriod = 26;
        macdSignalPeriod = 9;
    }

    public static List<String> getTimeframes() {
        return timeframes;
    }

    public static int getAmountOfCryptocurrency() {
        return AmountOfCryptocurrency;
    }

    public static int getCandlesAmount() {
        return CandlesAmount;
    }

    public static int getMacdShortPeriod() {
        return macdShortPeriod;
    }

    public static int getMacdLongPeriod() {
        return macdLongPeriod;
    }

    public static int getMacdSignalPeriod() {
        return macdSignalPeriod;
    }
}
