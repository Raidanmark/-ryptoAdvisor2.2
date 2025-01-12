package bot.data;

import bot.data.model.Ticker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TickerRepositoryTest {
    private TickerRepository tickerRepository;

    @BeforeEach
    void setUp() {
        tickerRepository = new TickerRepository();
    }

    @Test
    void testGetTicker() {
        List<Ticker> tickers = new ArrayList<>();
        Ticker ticker1 = new Ticker("BTCUSDT");
        Ticker ticker2 = new Ticker("ETHUSDT");
        tickers.add(ticker1);
        tickers.add(ticker2);

        tickerRepository.addTickers(tickers);

        List<Ticker> allTickers = tickerRepository.getAllTickers();
        assertEquals(2, allTickers.size());
        assertTrue(allTickers.contains(ticker1));
        assertTrue(allTickers.contains(ticker2));
    }

    @Test
    void testUpdateTicker() {
        Ticker ticker = new Ticker("BTCUSDT");
        tickerRepository.addTicker(List.of(ticker));
        ticker.setPrice(50000.0);
        tickerRepository.updateTicker(ticker);

        Ticker updatedTicker = tickerRepository.getAllTickers().get(0);
        assertEquals(50000.0, updatedTicker.getPrice());
    }

    @Test
    void testGetAllTicker() {
        Ticker ticker1 = new Ticker("BTCUSDT");
        Ticker ticker2 = new Ticker("ETHUSDT");
        tickerRepository.addTickers(List.of(ticker1, ticker2));
        assertEquals(2, allTickers.size());
        assertTrue(allTickers.contains(ticker1));
        assertTrue(allTickers.contains(ticker2));
    }
}