package bot.data.model;

public record WebSocketMessage(
   String ch,
   long ts,
   Kline candlestick
) {}
