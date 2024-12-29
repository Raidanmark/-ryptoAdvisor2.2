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

    public Data() {
        ObjectMapper om = new ObjectMapper()
                //this setting is for using JavaTimeModule for converting timestamps to Java time representations
                .registerModule(new JavaTimeModule());
        CloseableHttpClient client = HttpClients.createDefault();

        HuobiApi huobiApi = new HuobiApi(client, om);
        DataCollecting dataCollecting = new DataCollecting(huobiApi);

        // Инициализируем тикеры
        List<Ticker> initialTickers = dataCollecting.start();
        synchronized (allTickers) {
            allTickers.addAll(initialTickers);
        }
        System.out.println("Инициализация завершена. Загруженные тикеры: " + allTickers);



    }


}
