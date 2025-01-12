package bot.data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import bot.data.model.*;

public class DataCollecting {
    private final ApiClient apiClient;
    private final Websocket websocket;
    private final TickerRepository tickerRepository;
    private final DataConfig dataConfig;
    private final CandleFilter candleFilter;

    public DataCollecting(ApiClient apiClient, Websocket websocket, TickerRepository tickerRepository, DataConfig dataConfig, CandleFilter candleFilter) {
        this.apiClient = apiClient;
        this.websocket = websocket;
        this.tickerRepository = tickerRepository;
        this.dataConfig = dataConfig;
        this.candleFilter = candleFilter;
    }

    public List<Ticker> start() {
        try {

            List<MarketData> marketData = fetchMarketData();
            List<DOTMarketData> sortedTickers = sortAndLimitTickers(marketData);
            return fetchCandlestickData(sortedTickers);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Error fetching tickers", e);
        }
    }

    private List<MarketData> fetchMarketData() throws IOException, URISyntaxException {
        return apiClient.marketTickers(Collections.emptyMap());
    }

    private List<DOTMarketData> sortAndLimitTickers(List<MarketData> marketData) {
        return marketData.stream()
                .map(data -> new DOTMarketData(data.symbol(), data.vol()))
                .sorted(Comparator.comparingDouble(DOTMarketData::vol).reversed())
                .limit(dataConfig.getAmountOfCryptocurrency())
                .collect(Collectors.toList());
    }

    private List<Ticker> fetchCandlestickData(List<DOTMarketData> tickers) {
        List<Ticker> allTickers = new ArrayList<>();
        for (DOTMarketData ticker : tickers) {
            for (String timeframe : dataConfig.getTimeframes()) {
                try {
                    List<Kline> klineData = fetchKlines(ticker, timeframe);
                    allTickers.add(createTicker(ticker, timeframe, klineData));
                    setupWebsocketUpdates(ticker.symbol(), timeframe);
                } catch (IOException | URISyntaxException e) {
                    System.err.println("Error fetching candles for ticker: " + ticker.symbol());
                    e.printStackTrace();
                }
            }
        }
        return allTickers;
    }
    private List<Kline> fetchKlines(DOTMarketData ticker, String timeframe) throws IOException, URISyntaxException {
        Map<String, String> parameters = Map.of(
                "period", timeframe,
                "size", String.valueOf(dataConfig.getCandlesAmount()),
                "symbol", ticker.symbol()
        );
        return apiClient.getKlines(parameters);
    }

    private void setupWebsocketUpdates(String symbol, String timeframe) {
        websocket.updateCandlestick(symbol, timeframe, this::handleNewCandlestick);
    }


    private Ticker createTicker(DOTMarketData ticker, String timeframe, List<Kline> klineData) {
        LinkedList<Double> closePrices = klineData.stream()
                .map(Kline::close)
                .collect(Collectors.toCollection(LinkedList::new));

        long lastTimestamp = (long) klineData.get(klineData.size() - 1).id() * 1000;
        return new Ticker(ticker.symbol(), timeframe, closePrices, lastTimestamp, false, false);
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
