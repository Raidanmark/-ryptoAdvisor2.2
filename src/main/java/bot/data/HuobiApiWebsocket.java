package bot.data;

import com.huobi.client.MarketClient;
import com.huobi.client.req.market.SubCandlestickRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.CandlestickIntervalEnum;


import java.util.Arrays;

public class HuobiApiWebsocket {

    public void HuobiApiWebsocket(){

    }

    public <T> T updateCandlestick(String symbol, String timeframe){

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

    long candlestickTimestamp = candlestick.getTs(); // Получаем временную метку свечи

    if (CandleFilter.isCandleFinal(candlestickTimestamp, timeframe)) {
        System.out.println("Завершённая свеча: " + candlestick.toString());
    } else {
        System.out.println("Незавершённая свеча (обновление): " + candlestick.toString());
    }
    //Нужен метод для обработки времени когда пришел отклик от веб сокета
    //В нем тянем последние данные из бд ( он должны быть записаны уже в какуб то пременнуб для сравнения
    //и обновляться после записи подходящего для бд данных)
    //и затем сравниваем с веременем что пришло
    //Если подходит по таймфрейму то после переезаписи вызываем наши аналитические методы
    //для данного тикера и таймфрейма
    //от них выводим в бота результат
    //Добавляем необходимые комманды для управления ботом ивозможнростью выбора тикеров и таймфреймов и мб даже методов
    });
    return null;
    }
}
