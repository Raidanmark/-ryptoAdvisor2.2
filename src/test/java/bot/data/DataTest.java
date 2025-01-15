package bot.data;

import bot.analytics.TickerAnalyzer;
import bot.data.model.Ticker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataTest {
    private Data data;
    private TickerRepository mockRepository;
    private TickerAnalyzer mockAnalyzer;
    private DataCollecting mockCollecting;

    @BeforeEach
    void setUp() {
        mockRepository = mock(TickerRepository.class);
        mockAnalyzer = mock(TickerAnalyzer.class);
        mockCollecting = mock(DataCollecting.class);
        data = new Data(mockRepository, mockCollecting);
    }

    @Test
    void testStartLoadsAndAnalyzesTickers(){
        Ticker mockTicker = mock(Ticker.class);
        when(mockCollecting.start()).thenReturn(List.of(mockTicker));
        data.start();

        verify(mockRepository).addTickers(List.of(mockTicker));
        verify(mockAnalyzer).analyzeTicker(mockTicker);
    }

    @Test
    void testUpdateTickerUpdatesAndAnalyzes(){
        Ticker mockTicker = mock(Ticker.class);
        data.updateTicker(mockTicker);
        verify(mockRepository).updateTicker(mockTicker);
        verify(mockAnalyzer).analyzeTicker(mockTicker);
    }

    @Test
    void testAnalyzeTickerLogsErrorOnFailure(){
        Ticker mockTicker = mock(Ticker.class);
        doThrow(new RuntimeException("Analysis error"))
                .when(mockAnalyzer).analyzeTicker(mockTicker);

        data.updateTicker(mockTicker);
        verify(mockRepository).updateTicker(mockTicker);
        verify(mockAnalyzer).analyzeTicker(mockTicker);
    }

}