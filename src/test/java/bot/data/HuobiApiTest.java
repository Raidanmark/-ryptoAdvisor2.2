package bot.data;

import bot.data.model.ApiResponse;
import bot.data.model.Kline;
import bot.data.model.MarketData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HuobiApiTest {

    @Test
    void testGetKlines() throws Exception {
        // 1. Мокаем HTTP-клиент и поведение
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        HttpEntity mockEntity = mock(HttpEntity.class);
        org.apache.http.StatusLine mockStatusLine = mock(org.apache.http.StatusLine.class);

        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(200);

        String mockJson = """
        {
            "status": "ok",
            "data": [
                {"id": 123456, "amount": 10, "count": 2, "open": 100.0, "close": 110.0, "low": 95.0, "high": 115.0, "vol": 500}
            ]
        }
    """;
        InputStream mockInputStream = new ByteArrayInputStream(mockJson.getBytes());
        when(mockEntity.getContent()).thenReturn(mockInputStream);

        // 2. Создаём объект HuobiApi с мокаемыми зависимостями
        ObjectMapper objectMapper = new ObjectMapper();
        HuobiApi huobiApi = new HuobiApi(mockHttpClient, objectMapper);

        // 3. Вызываем метод getKlines
        Map<String, String> parameters = Map.of("symbol", "BTCUSDT", "period", "1min");
        List<Kline> result = huobiApi.getKlines(parameters);

        // 4. Проверяем результат
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123456, result.get(0).id());
        assertEquals(110.0, result.get(0).close());

        // 5. Проверяем вызов HTTP-клиента
        verify(mockHttpClient, times(1)).execute(any(HttpGet.class));
    }


    @Test
    void testMakeAPICall() throws Exception {
        // 1. Создаём моки для HTTP-клиента
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        HttpEntity mockEntity = mock(HttpEntity.class);
        org.apache.http.StatusLine mockStatusLine = mock(org.apache.http.StatusLine.class); // Мокаем StatusLine

        // 2. Настраиваем поведение мока
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine); // Возвращаем мок StatusLine
        when(mockStatusLine.getStatusCode()).thenReturn(200); // Настраиваем код статуса

        String mockJson = """
            {
                "status": "ok",
                "data": [
                    {"symbol": "BTCUSDT", "vol": 5000},
                    {"symbol": "ETHUSDT", "vol": 3000}
                ]
            }
        """;
        // Настраиваем InputStream с фиктивными данными
        InputStream mockInputStream = new ByteArrayInputStream(mockJson.getBytes());
        when(mockEntity.getContent()).thenReturn(mockInputStream);

        // 3. Создаём объект HuobiApi с мокаемыми зависимостями
        ObjectMapper objectMapper = new ObjectMapper();
        HuobiApi huobiApi = new HuobiApi(mockHttpClient, objectMapper);

        // 4. Тип данных, которые мы хотим получить
        TypeReference<ApiResponse<List<MarketData>>> typeReference = new TypeReference<>() {};

        // 5. Тестируемый метод
        String endpoint = "/market/tickers";
        Map<String, String> parameters = Map.of();
        List<MarketData> result = huobiApi.makeAPICall(endpoint, parameters, typeReference);

        // 6. Проверяем результат
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("BTCUSDT", result.get(0).symbol());
        assertEquals(5000, result.get(0).vol());

        // 7. Проверяем вызов HTTP-клиента
        verify(mockHttpClient, times(1)).execute(any(HttpGet.class));
    }
}