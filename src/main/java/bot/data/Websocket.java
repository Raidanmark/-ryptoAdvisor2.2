package bot.data;

public interface Websocket {
    void updateCandlestick(String symbol, String timeframe, CandlestickHandler handler);
}
