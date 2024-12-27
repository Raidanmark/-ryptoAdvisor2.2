package bot.data;

import bot.data.api.DataCollecting;
import bot.data.api.HuobiApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<Ticker> allData = new ArrayList<>();

    public Data() {
        ObjectMapper om = new ObjectMapper()
                //this setting is for using JavaTimeModule for converting timestamps to Java time representations
                .registerModule(new JavaTimeModule());
        CloseableHttpClient client = HttpClients.createDefault();

        HuobiApi huobiApi = new HuobiApi(client,om);
        DataCollecting dataCollecting = new DataCollecting(huobiApi);
        allData = dataCollecting.start();


    }



    public  List<Ticker> getAllData() {
        return allData;
    }

    public void setAllData(List<Ticker> allData) {
        this.allData = allData;
    }


}
