package bot.data;

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

    public Data() {
        this.data = this;
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

    public void updateTicker(Ticker updatedTicker) {
        synchronized (allTickers) {
            for (int i = 0; i < allTickers.size(); i++) {
                Ticker existingTicker = allTickers.get(i);
                if (existingTicker.symbol().equals(updatedTicker.symbol()) &&
                        existingTicker.timeframe().equals(updatedTicker.timeframe())) {
                    allTickers.set(i, updatedTicker); // Заменяем старый тикер на новый
                    return;
                }
            }
            throw new IllegalArgumentException("Ticker not found: " + updatedTicker.symbol() + " " + updatedTicker.timeframe());
        }
    }



}
