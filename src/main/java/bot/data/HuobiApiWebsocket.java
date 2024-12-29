package bot.data;

import bot.data.model.Kline;
import bot.data.model.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobi.client.MarketClient;
import com.huobi.client.req.market.SubCandlestickRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.CandlestickIntervalEnum;


import java.util.Arrays;

public class HuobiApiWebsocket {
    private final ObjectMapper objectMapper;
    private DataCollecting dataCollecting;

    public HuobiApiWebsocket(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

    }

    public <T> T updateCandlestick(String symbol, String timeframe, DataCollecting dataCollecting) {


        this.dataCollecting = dataCollecting;
        // Поиск значения enum по code через Stream
        CandlestickIntervalEnum enumtimeframe =
                Arrays.stream(CandlestickIntervalEnum.values())
                        .filter(e -> e.getCode().equals(timeframe))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Invalid timeframe: " + timeframe));

        MarketClient marketClient = MarketClient.create(new HuobiOptions());
marketClient.subCandlestick(SubCandlestickRequest.builder()
        .symbol(symbol)
    .interval(enumtimeframe)
    .build(), (candlestick) -> {

    WebSocketMessage message = objectMapper.convertValue(candlestick, WebSocketMessage.class);
    String channel = message.ch();// "market.ethbtc.kline.1min"
    long timestamp = message.ts();
    Kline kline = message.candlestick();  // Данные свечи
    dataCollecting.handleNewCandlestick(kline, channel, timestamp);
    //Kline kline  = objectMapper.convertValue(candlestick, Kline.class);
    System.out.println(" свеча: " + candlestick.toString());



    });
    return null;
    }

}
//Нужен метод для обработки времени когда пришел отклик от веб сокета
//В нем тянем последние данные из бд ( он должны быть записаны уже в какуб то пременнуб для сравнения
//и обновляться после записи подходящего для бд данных)
//и затем сравниваем с веременем что пришло
//Если подходит по таймфрейму то после переезаписи вызываем наши аналитические методы
//для данного тикера и таймфрейма
//от них выводим в бота результат
//Добавляем необходимые комманды для управления ботом ивозможнростью выбора тикеров и таймфреймов и мб даже методов