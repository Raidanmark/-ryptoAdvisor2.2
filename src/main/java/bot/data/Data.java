package bot.data;

import bot.analytics.MACD;
import bot.analytics.SMA;
import bot.chatbot.BotCore;
import bot.data.model.Ticker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private final List<Ticker> allTickers = new ArrayList<>();
    Data data;
    SMA sma;
    MACD macd;
    BotCore botCore;

    public Data(BotCore botCore) {
        this.data = this;
        this.botCore = botCore;
        this.sma = new SMA(data, botCore.getBotListener());
        this.macd = new MACD(data, botCore.getBotListener());

        ObjectMapper om = new ObjectMapper()
                //this setting is for using JavaTimeModule for converting timestamps to Java time representations
                .registerModule(new JavaTimeModule());
        CloseableHttpClient client = HttpClients.createDefault();

        HuobiApi huobiApi = new HuobiApi(client, om);
        HuobiApiWebsocket huobiApiWebsocket = new HuobiApiWebsocket(om);
        DataCollecting dataCollecting = new DataCollecting(huobiApi,huobiApiWebsocket, data);

        // Инициализируем тикеры
        List<Ticker> initialTickers = dataCollecting.start();
        synchronized (allTickers) {
            allTickers.addAll(initialTickers);
        }
        System.out.println("Инициализация завершена. Загруженные тикеры: " + allTickers);
        analizeLoadedTickers();
    }

    public synchronized List<Ticker> getAllTickers() {
        return new ArrayList<>(allTickers); // Возвращаем копию списка для избежания модификации оригинала
    }

    public Ticker findTicker(String symbol, String timeframe) {
        synchronized (allTickers) {
            for (Ticker ticker : allTickers) {
                if (ticker.symbol().equals(symbol) && ticker.timeframe().equals(timeframe)) {
                    return ticker; // Возвращаем найденный тикер
                }
            }
        }
        return null; // Если тикер не найден
    }

    public void analizeLoadedTickers() {
        synchronized (data.getAllTickers()) {
            for (Ticker ticker : data.getAllTickers()) {
                // Анализируем SMA
                runAnalyticsForTicker(ticker);
            }
        }
    }

    public void updateTicker(Ticker updatedTicker) {
        synchronized (allTickers) {
            for (int i = 0; i < allTickers.size(); i++) {
                Ticker existingTicker = allTickers.get(i);
                if (existingTicker.symbol().equals(updatedTicker.symbol()) &&
                        existingTicker.timeframe().equals(updatedTicker.timeframe())) {
                    // Заменяем старый тикер на новый
                    allTickers.set(i, updatedTicker);

                    // Проводим анализы для обновленного тикера
                    runAnalyticsForTicker(updatedTicker);

                    return;
                }
            }
            throw new IllegalArgumentException("Ticker not found: " + updatedTicker.symbol() + " " + updatedTicker.timeframe());
        }
    }

    public void runAnalyticsForTicker(Ticker ticker) {
        // Анализ SMA
        sma.analyzeSMASignal(ticker);
        // Анализ MACD
        macd.analyzeMACDSignal(ticker);

    }
}
