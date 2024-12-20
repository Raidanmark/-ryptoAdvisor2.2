package bot.data;

import bot.data.api.DataCollecting;
import bot.data.api.HuobiApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Data {
    public Data() {
        ObjectMapper om = new ObjectMapper()
                //this setting is for using JavaTimeModule for converting timestamps to Java time representations
                .registerModule(new JavaTimeModule());
        CloseableHttpClient client = HttpClients.createDefault();

        HuobiApi huobiApi = new HuobiApi(client,om);
        DataCollecting dataCollecting = new DataCollecting(huobiApi);
        dataCollecting.start();
    }
}
