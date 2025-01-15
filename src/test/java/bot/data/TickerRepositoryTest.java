package bot.data;

import bot.data.model.Ticker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
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
        LinkedList<Double> prices = new LinkedList<>(List.of(
                100.0, 102.0, 104.0, 106.0, 108.0, 110.0, 112.0, 114.0, 116.0, 118.0
        ));
        Ticker ticker1 = new Ticker("BTCUSDT", "15min", prices,0L, false, false);
        Ticker ticker2 = new Ticker("ETHUSDT", "15min", new LinkedList<>(),0L, false, false);
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
        LinkedList<Double> prices = new LinkedList<>(List.of(
                100.0, 102.0, 104.0, 106.0, 108.0, 110.0, 112.0, 114.0, 116.0, 118.0
        ));
        Ticker ticker = new Ticker("BTCUSDT", "15min", prices,0L, false, false);
        tickerRepository.addTickers(List.of(ticker));
        ticker.addClosePrice(50000.0, 10);
        tickerRepository.updateTicker(ticker);

        Ticker updatedTicker = tickerRepository.getAllTickers().get(0);
        assertEquals(50000.0, updatedTicker.close().getLast());
    }

    @Test
    void testGetAllTicker() {
        LinkedList<Double> prices = new LinkedList<>(List.of(
                100.0, 102.0, 104.0, 106.0, 108.0, 110.0, 112.0, 114.0, 116.0, 118.0
        ));
        Ticker ticker1 = new Ticker("BTCUSDT", "15min", prices,0L, false, false);
        Ticker ticker2 = new Ticker("ETHUSDT", "15min", new LinkedList<>(),0L, false, false);
        tickerRepository.addTickers(List.of(ticker1, ticker2));
        List<Ticker> allTickers = tickerRepository.getAllTickers();
        assertEquals(2, allTickers.size());
        assertTrue(allTickers.contains(ticker1));
        assertTrue(allTickers.contains(ticker2));
    }


}