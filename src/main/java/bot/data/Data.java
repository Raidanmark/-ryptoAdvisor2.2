package bot.data;

import bot.data.api.CryptoDataCollector;
import bot.data.api.HuobiApiServiceImpl;

import java.util.Map;
import java.util.Deque;

public class Data {
    public Data() {
        try {
            HuobiApiServiceImpl huobiApiService = new HuobiApiServiceImpl();
            CryptoDataCollector collector = new CryptoDataCollector(huobiApiService);
            Map<String, Map<String, Deque<Price>>> allData = collector.collectData();

            if (allData.isEmpty()) {
                System.out.println("Данные не были собраны. Проверьте настройки API или фильтры.");
            } else {
                System.out.println("Сбор данных завершён. Размер allData: " + allData.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
