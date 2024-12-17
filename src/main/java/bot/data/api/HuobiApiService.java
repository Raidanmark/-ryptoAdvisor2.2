package bot.data.api;

import bot.data.Price;
import bot.data.Cryptocurrency;

import java.util.List;

public interface HuobiApiService {
    List<Cryptocurrency> getTopCryptocurrencies(int limit) throws Exception;

    List<Price> getHistoricalPrices(String symbol, String timeframe, int size) throws Exception;
}
