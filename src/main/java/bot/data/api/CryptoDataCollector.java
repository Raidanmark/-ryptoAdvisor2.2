package bot.data.api;

import bot.data.Cryptocurrency;
import bot.data.DataConfig;
import bot.data.Price;
import bot.data.Timeframe;

import java.util.*;

public class CryptoDataCollector {
    private HuobiApiService huobiApiService;
    DataConfig dataConfig = new DataConfig();

    public CryptoDataCollector(HuobiApiService huobiApiService) {
        this.huobiApiService = huobiApiService;
    }

    public Map<String, Map<String, Deque<Price>>> collectData() {
        Map<String, Map<String, Deque<Price>>> allData = new HashMap<>();
        Timeframe tf = new Timeframe();

        try {
            // Получаем топ криптовалют
            List<Cryptocurrency> topCryptos = huobiApiService.getTopCryptocurrencies(dataConfig.getAmountOfCryptocurrency());
            System.out.println("Получено символов: " + topCryptos.size());

            if (topCryptos.isEmpty()) {
                System.out.println("Список символов пуст. Проверьте API.");
                return allData;
            }

            // Выводим топ криптовалют
            System.out.println("Топ криптовалют перед записью в allData:");
            for (Cryptocurrency crypto : topCryptos) {
                System.out.printf("Тикер: %s, Капитализация: %.2f%n", crypto.getSymbol(), crypto.getVolume());
            }

            // Обрабатываем тикеры
            for (Cryptocurrency crypto : topCryptos) {
                String symbol = crypto.getSymbol();
                System.out.println("Обрабатываем символ: " + symbol);


                allData.putIfAbsent(symbol, new HashMap<>());

                for (String timeframe : tf.getApiTimeframes()) {
                    try {
                        System.out.println("  Таймфрейм: " + timeframe);
                        List<Price> prices = huobiApiService.getHistoricalPrices(symbol, timeframe, dataConfig.getCandlesAmount());
                        System.out.println("  Найдено цен: " + prices.size());

                        if (!prices.isEmpty()) {
                            Deque<Price> priceQueue = allData.get(symbol).computeIfAbsent(timeframe, k -> new ArrayDeque<>());
                            for (Price price : prices) {
                                if (priceQueue.size() >= dataConfig.getCandlesAmount()) {
                                    priceQueue.pollFirst();
                                }
                                priceQueue.addLast(price);
                            }
                        } else {
                            System.out.println("  Нет данных для символа: " + symbol + ", таймфрейм: " + timeframe);
                        }
                    } catch (Exception e) {
                        System.err.println("Ошибка для символа " + symbol + ", таймфрейм " + timeframe + ": " + e.getMessage());
                    }
                }

                // Проверяем, добавились ли данные
                System.out.println("allData для тикера " + symbol + ": " + allData.get(symbol));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allData;
    }


    public void displayTopCryptocurrencies(List<Cryptocurrency> cryptocurrencies) {
        System.out.println("Топ криптовалют по капитализации:");
        for (Cryptocurrency crypto : cryptocurrencies) {
            System.out.printf("Тикер: %s, Капитализация: %.2f%n", crypto.getSymbol(), crypto.getVolume());
        }
    }
}
