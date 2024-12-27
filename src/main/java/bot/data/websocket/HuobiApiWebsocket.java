package bot.data.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobi.client.MarketClient;
import com.huobi.client.req.market.CandlestickRequest;
import com.huobi.client.req.market.SubCandlestickRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.CandlestickIntervalEnum;
import com.huobi.model.market.Candlestick;
import org.apache.http.impl.client.CloseableHttpClient;


import java.util.Arrays;
import java.util.List;

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
    });
    return null;
    }
}
