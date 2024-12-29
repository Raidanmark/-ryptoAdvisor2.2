package bot.data.model;



public record Kline(
        double id,
        double amount,
        int count,
        double open,
        double close,
        double low,
        double  high,
        double vol

) {}
