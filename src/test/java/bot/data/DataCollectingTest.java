package bot.data;

import bot.data.model.Kline;
import bot.data.model.Ticker;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataCollectingTest {
    @Test
    void testHandleNewCandlestickUpdatesTicker() {
        // Создаем моки
        ApiClient mockApiClient = mock(ApiClient.class);
        Websocket mockWebsocket = mock(Websocket.class);
        TickerRepository mockRepository = mock(TickerRepository.class);
        CandleFilter mockFilter = mock(CandleFilter.class);
        DataConfig mockConfig = mock(DataConfig.class);

        // Подготавливаем данные
        Ticker ticker = new Ticker("BTCUSDT", "15min", new LinkedList<>(), 123456L, false, false);
        when(mockRepository.findTicker("BTCUSDT", "15min")).thenReturn(ticker); // Метод на mock

        when(mockFilter.getIntervalDuration("15min")).thenReturn(900000L);
        when(mockConfig.getCandlesAmount()).thenReturn(40);

        Kline kline = new Kline(1, 1000.0, 10, 49500.0, 50000.0, 49000.0, 50500.0, 500.0);
        long newTimestamp = 123456000L + 900001L;

        // Создаем объект DataCollecting
        DataCollecting dataCollecting = new DataCollecting(mockApiClient, mockWebsocket, mockRepository, mockConfig, mockFilter);

        // Вызываем метод
        dataCollecting.handleNewCandlestick(kline, "market.BTCUSDT.kline.15min", newTimestamp);

        // Проверяем, что тикер обновлён
        ArgumentCaptor<Ticker> captor = ArgumentCaptor.forClass(Ticker.class);
        verify(mockRepository).updateTicker(captor.capture());
        Ticker updatedTicker = captor.getValue();

        // Ассерты
        assertEquals("BTCUSDT", updatedTicker.symbol());
        assertEquals(50000.0, updatedTicker.close().getLast());
        assertEquals(newTimestamp / 1000, updatedTicker.lastTimestamp());
    }

}