package bot.analytics;

import bot.data.model.Ticker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.mockito.Mockito.*;

class TickerAnalyzerTest {
    private TickerAnalyzer tickerAnalyzer;

    @BeforeEach
    void setUp() {
        tickerAnalyzer = new TickerAnalyzer();
    }

    @Test
    void testAnalyzeTickerCallsBothMethods(){
        Ticker mockTicker = mock(Ticker.class);
        when(mockTicker.symbol()).thenReturn("BTCUSDT");

        TickerAnalyzer spyAnalyzer = spy(new TickerAnalyzer());

        spyAnalyzer.analyzeTicker(mockTicker);

        verify(spyAnalyzer, times(1)).analyzeMACD(mockTicker);
        verify(spyAnalyzer, times(1)).analyzeSMA(mockTicker);
    }

    @Test
    void testAnalyzeTickerHandlesErrorInMACD(){
        // Мокаем тикер
        Ticker mockTicker = mock(Ticker.class);
        when(mockTicker.symbol()).thenReturn("BTCUSDT");

        TickerAnalyzer spyAnalyzer = spy(new TickerAnalyzer());

        // Симулируем ошибку в analyzeMACD
        doThrow(new RuntimeException("MACD Error")).when(spyAnalyzer).analyzeMACD(mockTicker);

        // Вызываем анализ
        spyAnalyzer.analyzeTicker(mockTicker);

        // Проверяем, что SMA все равно был вызван
        verify(spyAnalyzer, times(1)).analyzeMACD(mockTicker);
        verify(spyAnalyzer, times(1)).analyzeSMA(mockTicker);
    }

    @Test
    void testLogErrorOnAnalysisFailure() {
        Ticker mockTicker = mock(Ticker.class);
        when(mockTicker.symbol()).thenReturn("BTCUSDT");
        TickerAnalyzer spyAnalyzer = spy(new TickerAnalyzer());
        doThrow(new RuntimeException("MACD Error")).when(spyAnalyzer).analyzeMACD(mockTicker);
        spyAnalyzer.analyzeTicker(mockTicker);
    }
}