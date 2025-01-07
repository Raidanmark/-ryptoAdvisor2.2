package bot.data;

import bot.data.model.Kline;
import bot.data.model.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobi.client.MarketClient;
import com.huobi.client.req.market.SubCandlestickRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.CandlestickIntervalEnum;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class HuobiApiWebsocket {
    private final ObjectMapper objectMapper;
    private DataCollecting dataCollecting;

    public HuobiApiWebsocket(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void updateCandlestick(String symbol, String timeframe, CandlestickHandler handler) {
        MarketClient marketClient = MarketClient.create(new HuobiOptions());
        CandlestickIntervalEnum enumtimeframe = Arrays.stream(CandlestickIntervalEnum.values())
                .filter(e -> e.getCode().equals(timeframe))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid timeframe: " + timeframe));

        marketClient.subCandlestick(SubCandlestickRequest.builder()
                .symbol(symbol)
                .interval(enumtimeframe)
                .build(), (candlestick) -> {
            WebSocketMessage message = objectMapper.convertValue(candlestick, WebSocketMessage.class);
            Kline kline = message.candlestick();
            String channel = message.ch();
            long timestamp = message.ts();
            handler.onNewCandlestick(kline, channel, timestamp); // Вызываем через CandlestickHandler
        });
    }

}
