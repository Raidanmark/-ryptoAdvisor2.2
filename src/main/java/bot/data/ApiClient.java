package bot.data;

import bot.data.model.Kline;
import bot.data.model.MarketData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface ApiClient {
    List<MarketData> marketTickers(Map<String, String> parameters) throws IOException, URISyntaxException;

    List<Kline> getKlines(Map<String, String> parameters) throws IOException, URISyntaxException;
}
