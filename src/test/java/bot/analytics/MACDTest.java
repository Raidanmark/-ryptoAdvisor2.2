package bot.analytics;

import bot.chatbot.BotListener;
import bot.data.Data;
import bot.data.model.Ticker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MACDTest {

    private MACD macd;
    private Data mockData;
    private BotListener mockListener;

    @BeforeEach
    void setUp() {
        mockData = mock(Data.class);
        mockListener = mock(BotListener.class);
        macd = new MACD(mockData, mockListener);
    }

    @Test
    void testCalculateMACD() {
        LinkedList<Double> prices = new LinkedList<>(List.of(
                100.0, 102.0, 104.0, 106.0, 108.0, 110.0, 112.0, 114.0, 116.0, 118.0,
                120.0, 122.0, 124.0, 126.0, 128.0, 130.0, 132.0, 134.0, 136.0, 138.0,
                140.0, 142.0, 144.0, 146.0, 148.0, 150.0, 152.0, 154.0, 156.0, 158.0,
                160.0, 162.0, 164.0, 166.0, 168.0, 170.0, 172.0, 174.0, 176.0, 178.0
        ));

        Ticker ticker = new Ticker("BTCUSDT", "15min", prices, 0L, false, false);

        macd.analyze(ticker);


        // Проверяем, что новый Ticker передан в updateTicker
        verify(mockData, times(1)).updateTicker(argThat(updatedTicker ->
                updatedTicker.symbol().equals("BTCUSDT") &&
                        updatedTicker.MACDsignal()
        ));

        // Проверяем, что сообщение было отправлено
        verify(mockListener, times(1)).broadcastMessage(contains("MACD signal: BUY"));
        //assertTrue(ticker.MACDsignal());

        //verify(mockData, times(1)).updateTicker(ticker);
        //verify(mockListener, times(1)).broadcastMessage((contains("MACD signal: BUY")));

    }

}