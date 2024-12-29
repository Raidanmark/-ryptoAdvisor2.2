package bot.data.model;

public record MarketData(
        String symbol,
        double open,
        double high,
        double low,
        double close,
        double amount,
        double vol,
        int count,
        double bid,
        double bidSize,
        double ask,
        double askSize
) {}