package bot.data;

import bot.data.model.Kline;

public interface CandlestickHandler {
    void onNewCandlestick(Kline kline, String channel, long timestamp);
}
