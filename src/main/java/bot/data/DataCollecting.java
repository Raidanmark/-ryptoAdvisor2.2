package bot.data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import bot.data.model.*;

public class DataCollecting {
    private final HuobiApi huobiApi;
    private final HuobiApiWebsocket huobiApiWebsocket;
    private final TickerRepository tickerRepository;
    private final DataConfig dataConfig;
    private final CandleFilter candleFilter;

    public DataCollecting(HuobiApi huobiApi, HuobiApiWebsocket huobiApiWebsocket, TickerRepository tickerRepository, DataConfig dataConfig, CandleFilter candleFilter) {
        this.huobiApi = huobiApi;
        this.huobiApiWebsocket = huobiApiWebsocket;
        this.tickerRepository = tickerRepository;
        this.dataConfig = dataConfig;
        this.candleFilter = candleFilter;
    }

    public List<Ticker> start() {

// Получение тикеров
        try {
// 1. Получение списка всех тикеров с биржи
            var tickers = huobiApi.marketTickers(Collections.emptyMap());
            List<DOTMarketData> volData = pullVol(tickers);

            // 2. Сортировка тикеров по объёму
            volData.sort(Comparator.comparingDouble(DOTMarketData::vol).reversed());

            // 3. Ограничение списка тикеров до N из конфигурации
            volData = keepTop(volData, dataConfig.getAmountOfCryptocurrency());

            // 4. Генерация данных свечей для тикеров по каждому таймфрейму
            List<Ticker> allTickers = getCandles(volData, dataConfig.getTimeframes(), dataConfig.getCandlesAmount());

            // 5. Возвращаем итоговый список тикеров
            return allTickers;

        } catch (IOException | URISyntaxException e) {
            // Логируем ошибку и выбрасываем исключение
            throw new RuntimeException("Ошибка получения данных о тикерах", e);
        }
    }

    // Оставляет только топ N тикеров по объёму
    private List<DOTMarketData> keepTop(List<DOTMarketData> tickers, int amount) {
        return tickers.size() > amount ? tickers.subList(0, amount) : tickers;
    }

    // Преобразует MarketData в DOTMarketData с упрощением структуры
    private List<DOTMarketData> pullVol(List<MarketData> tickers) {
        return tickers.stream()
                .map(data -> new DOTMarketData(data.symbol(), data.vol()))
                .collect(Collectors.toList());
    }

    // Генерирует данные свечей (candles) для тикеров по всем таймфреймам
    private List<Ticker> getCandles(List<DOTMarketData> tickers, List<String> timeframes, int candles) {
        List<Ticker> allTickers = new ArrayList<>();

        for (DOTMarketData ticker : tickers) {
            for (String timeframe : timeframes) {
                try {
                    // Параметры запроса к API
                    Map<String, String> parameters = Map.of(
                            "period", timeframe,
                            "size", String.valueOf(candles),
                            "symbol", ticker.symbol()
                    );

                    // Получаем данные свечей через API
                    var klineData = huobiApi.getKlines(parameters);
                    LinkedList<Double> closePrices = new LinkedList<>(klineData.stream()
                            .map(Kline::close)
                            .collect(Collectors.toList()));

                    long lastTimestamp = (long) klineData.get(klineData.size() - 1).id() * 1000;
                    allTickers.add(new Ticker(ticker.symbol(), timeframe, closePrices, lastTimestamp, false, false));

                    // Настраиваем WebSocket для обновлений данных
                    huobiApiWebsocket.updateCandlestick(
                            ticker.symbol(),
                            timeframe,
                            this::handleNewCandlestick // Передаём метод handleNewCandlestick напрямую
                    );


                } catch (IOException | URISyntaxException e) {
                    System.err.println("Ошибка получения свечей для тикера: " + ticker.symbol());
                    e.printStackTrace();
                }
            }
        }

        return allTickers;
    }


    // Обрабатывает обновления данных через WebSocket
    private void handleNewCandlestick(Kline kline, String channel, long timestamp) {
        String[] parts = channel.split("\\.");
        String symbol = parts[1];
        String timeframe = parts[3];

        // Находим тикер в репозитории
        Ticker ticker = tickerRepository.findTicker(symbol, timeframe);
        if (ticker != null) {
            // Проверяем, нужно ли обновлять данные тикера
            long adjustedTimestamp = ticker.lastTimestamp() * 1000 + candleFilter.getIntervalDuration(timeframe);

            if (timestamp > adjustedTimestamp) {
                ticker.addClosePrice(kline.close(), dataConfig.getCandlesAmount());
                Ticker updatedTicker = new Ticker(
                        symbol, timeframe, ticker.close(), timestamp / 1000, ticker.SMAsignal(), ticker.MACDsignal()
                );
                tickerRepository.updateTicker(updatedTicker);
                System.out.println("Тикер обновлён: " + symbol + " (" + timeframe + ")");
            } else {
                System.out.println("Старые данные, обновление не требуется.");
            }
        } else {
            System.err.println("Тикер не найден: " + symbol + " и таймфрейм: " + timeframe);
        }
    }
}
