package bot.data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import bot.data.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Collectors;

public class DataCollecting {
    HuobiApi huobiApi;
    DataConfig dataConfig = new DataConfig();
    HuobiApiWebsocket huobiApiWebsocket;
    DataCollecting dataCollecting;
    Data data;
    CandleFilter candleFilter;


    public DataCollecting(HuobiApi huobiApi, HuobiApiWebsocket huobiApiWebsocket, Data data) {

        this.dataCollecting = this;
        this.huobiApi = huobiApi;
        this.huobiApiWebsocket = huobiApiWebsocket;
        this.data = data;
    }


    public List<Ticker> start() {
        List<Ticker> allTickers = new ArrayList<>();
        List<DOTMarketData> volData = new ArrayList<>();
// Получение тикеров
        try {
            var tickers = huobiApi.marketTickers(Collections.emptyMap());
           // System.out.println(tickers);
            volData = pullVol(tickers);
             System.out.println(volData);
            Collections.sort(volData, Comparator.comparingDouble(DOTMarketData::vol).reversed());
            volData = keepTop(volData,dataConfig.getAmountOfCryptocurrency());
            System.out.println(volData);

            allTickers = getCandles(volData, dataConfig.getTimeframes(),dataConfig.getAmountOfCryptocurrency());
            System.out.println(allTickers);





        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }


        //(в коллектинг) сортировка тикеров по объему и выборка фикс количества


        //(в коллектинг) заполнения карты параметров для создания урла получения массива цен закрытия

        //(тут) вызов метода для получения и (в коллектинг)заполнения данных по всем тикерам

        //
        return allTickers;

    }

    private List<DOTMarketData> keepTop(List<DOTMarketData> tickers, int amount) {
        if (tickers.size() > amount) {
            return tickers.subList(0, amount);
        }
        return tickers;
    }


    private List<DOTMarketData> pullVol(List<MarketData> tickers) {

        List<DOTMarketData> tickersVol = tickers.stream()
                .map(data -> new DOTMarketData(
                        data.symbol(),
                        data.vol()
                ))
                .collect(Collectors.toList());
        return tickersVol;
    }

    private  List<Ticker> getCandles(List<DOTMarketData> tickers, List<String> timeframes, int candles) {
         List<Ticker> allTickers = new ArrayList<>();
         Map<String, String> parameters = new HashMap<>();
         LinkedList<Double> closePrices = new LinkedList<>();

         String candlesamount = String.valueOf(candles);
         // перебор всех тикеров и таймфреймов для них
         for (DOTMarketData ticker : tickers) {
             for (String timeframe : timeframes) {

                 //Input parameters
                 parameters.put("period", timeframe);
                 parameters.put("size", candlesamount);
                 parameters.put("symbol", ticker.symbol());

                 try {
                     var allkliens = huobiApi.getKlines(parameters);
                     long lastTimestamp = (long) allkliens.get(allkliens.size() - 1).id() * 1000; // Берём ID последней свечи
                     closePrices = new LinkedList<>(allkliens.stream()
                             .map(Kline::close)
                             .collect(Collectors.toList()));

                     allTickers.add(new Ticker(ticker.symbol(), timeframe, closePrices, lastTimestamp));

                 } catch (IOException | URISyntaxException e) {
                     throw new RuntimeException(e);
                 }

                 parameters.clear();



                 try {
                     var websocketupdate = huobiApiWebsocket.updateCandlestick(ticker.symbol(), timeframe, dataCollecting);
                     System.out.println(websocketupdate);

                 } catch (RuntimeException e){

                     System.err.println("Ошибка при выполнении updateCandlestick: " + e.getMessage());
                     e.printStackTrace();
                 }
             }


         }

         return allTickers;
    }

    public void handleNewCandlestick(Kline kline, String channel, long timestamp) {
        String[] parts = channel.split("\\.");

        // Извлекаем символ и таймфрейм
        String symbol = parts[1];
        String timeframe = parts[3];
        long ts = timestamp;

        Ticker ticker = data.findTicker(symbol, timeframe);
        if (ticker != null) {
            // Приведение lastTimestamp в миллисекунды
            long adjustedTimestamp = ticker.lastTimestamp() * 1000 + candleFilter.getIntervalDuration(timeframe);

            // Логирование для отладки
            System.out.println("Received ts: " + ts);
            System.out.println("Ticker lastTimestamp (ms): " + ticker.lastTimestamp() * 1000);
            System.out.println("Calculated adjustedTimestamp: " + adjustedTimestamp);

            // Сравнение таймстампов
            if (ts > adjustedTimestamp) {
                ticker.addClosePrice(kline.close(), dataConfig.getCandlesAmount());

                // Обновление lastTimestamp в секундах
                Ticker updatedTicker = new Ticker(symbol, timeframe, ticker.close(), ts / 1000);
                data.updateTicker(updatedTicker);

                System.out.println("Данные тикера обновлены: " + symbol + " (" + timeframe + ")");
            } else {
                System.out.println("Старые данные, обновление не требуется.");
            }
        } else {
            System.err.println("Тикер не найден для символа: " + symbol + " и таймфрейма: " + timeframe);
        }
    }




}
