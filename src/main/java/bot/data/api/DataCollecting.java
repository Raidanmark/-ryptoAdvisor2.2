package bot.data.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import bot.data.DataConfig;
import bot.data.api.model.DOTMarketData;
import bot.data.api.model.Kline;
import bot.data.api.model.MarketData;
import bot.data.api.HuobiApi;
import bot.data.api.model.Ticker;

import java.util.stream.Collectors;

public class DataCollecting {
    HuobiApi huobiApi;
    DataConfig dataConfig = new DataConfig();

    public DataCollecting(HuobiApi huobiApi) {
        this.huobiApi = huobiApi;
    }


    public void start() {
        List<Ticker>  allData = new ArrayList<>();
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

            allData = getCandles(volData, dataConfig.getTimeframes(),dataConfig.getAmountOfCryptocurrency());
            System.out.println(allData);



        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }


        //(в коллектинг) сортировка тикеров по объему и выборка фикс количества


        //(в коллектинг) заполнения карты параметров для создания урла получения массива цен закрытия

        //(тут) вызов метода для получения и (в коллектинг)заполнения данных по всем тикерам

        //

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
         List<Double> closePrices = new ArrayList<>();

         String candlesamount = String.valueOf(candles);
         // перебор всех тикеров и таймфреймов для них
         for (DOTMarketData ticker : tickers) {
             for (String timeframe : timeframes) {

                 //Input parameters
                 parameters.put("period", timeframe);
                 parameters.put("size", candlesamount);
                 parameters.put("symbol", ticker.symbol());

                 try{
                     var allkliens = huobiApi.getKlines(parameters);
                     closePrices = allkliens.stream()
                             .map(Kline::close)
                             .collect(Collectors.toList());

                     allTickers.add(new Ticker(ticker.symbol(), timeframe, closePrices));

                 } catch (IOException | URISyntaxException e) {
                     throw new RuntimeException(e);
                 }

                 parameters.clear();

                 // Создаём Ticker с этими значениями



             }
         }

         return allTickers;
    }

}
