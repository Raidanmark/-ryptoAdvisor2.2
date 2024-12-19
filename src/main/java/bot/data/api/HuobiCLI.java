package bot.data.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Scanner;
public class HuobiCLI {
            HuobiApi huobiApi;

        public HuobiCLI(HuobiApi huobiApi) {
            this.huobiApi = huobiApi;
        }



    public void start() {

// Получение тикеров
                try {
                    var tickers = huobiApi.marketTickers(Collections.emptyMap());
                    System.out.println(tickers);

                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
        //сортировка тикеров по объему и выборка фикс количества

        //заполнения карты параметров для создания урла получения массива цен закрытия

        //вызов метода для получения и заполнения данных по всем тикерам

    }
}


