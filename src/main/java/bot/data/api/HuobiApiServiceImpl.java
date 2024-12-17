package bot.data.api;

import bot.data.Cryptocurrency;
import bot.data.Price;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HuobiApiServiceImpl implements HuobiApiService {
    private static final String BASE_URL = "https://api.huobi.pro";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Set<String> availableSymbols;

    public HuobiApiServiceImpl() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.availableSymbols = new HashSet<>();
        try {
            loadAvailableSymbols();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAvailableSymbols() throws Exception {
        String url = BASE_URL + "/v1/common/symbols";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode rootNode = objectMapper.readTree(response.body());

        if (rootNode.has("data")) {
            JsonNode dataNode = rootNode.get("data");
            for (JsonNode node : dataNode) {
                String symbol = node.get("symbol").asText();
                availableSymbols.add(symbol);
            }
        } else {
            throw new Exception("Не удалось загрузить список доступных символов с Huobi");
        }
    }


    @Override
    public List<Cryptocurrency> getTopCryptocurrencies(int limit) throws Exception {
        System.out.println("Вызов API для получения топовых криптовалют...");

        String url = BASE_URL + "/market/tickers";
        System.out.println("URL запроса: " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Ответ API (полный): " + response.body());

        JsonNode rootNode = objectMapper.readTree(response.body());
        if (rootNode.has("data")) {
            JsonNode dataNode = rootNode.get("data");
            System.out.println("Найдено записей: " + dataNode.size());

            List<Cryptocurrency> cryptocurrencies = new ArrayList<>();
            for (JsonNode node : dataNode) {
                if (node.has("symbol") && node.has("vol")) {
                    String symbol = node.get("symbol").asText();
                    double volume = node.get("vol").asDouble();

                    cryptocurrencies.add(new Cryptocurrency(symbol, volume));
                    System.out.println("Добавлен символ: " + symbol + ", капитализация: " + volume);
                } else {
                    System.out.println("Пропущен элемент из-за отсутствия полей: " + node);
                }
            }

            // Проверяем, есть ли данные перед сортировкой
            if (cryptocurrencies.isEmpty()) {
                System.out.println("Список криптовалют пуст после парсинга.");
            } else {
                System.out.println("Список криптовалют перед сортировкой:");
                for (Cryptocurrency crypto : cryptocurrencies) {
                    System.out.printf("Тикер: %s, Капитализация: %.2f%n", crypto.getSymbol(), crypto.getVolume());
                }

                // Сортировка по капитализации
                cryptocurrencies.sort((a, b) -> Double.compare(b.getVolume(), a.getVolume()));

                // Проверяем топ после сортировки
                System.out.println("Топ криптовалют после сортировки:");
                for (Cryptocurrency crypto : cryptocurrencies) {
                    System.out.printf("Тикер: %s, Капитализация: %.2f%n", crypto.getSymbol(), crypto.getVolume());
                }

                cryptocurrencies.subList(0, Math.min(limit, cryptocurrencies.size()));
                System.out.println("Топ " + limit + " криптовалют:");
                cryptocurrencies.forEach(System.out::println);

                return cryptocurrencies;
            }
        } else {
            System.out.println("Некорректный ответ от API Huobi. Поле 'data' отсутствует.");
        }

        return Collections.emptyList();
    }


    @Override
    public List<Price> getHistoricalPrices(String symbol, String timeframe, int size) throws Exception {
        String url = String.format("%s/market/history/kline?symbol=%s&period=%s&size=%d", BASE_URL, symbol, timeframe, size);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode rootNode = objectMapper.readTree(response.body());

        if (rootNode.has("data") && rootNode.get("data").size() > 0) {
            JsonNode dataNode = rootNode.get("data");
            List<Price> prices = new ArrayList<>();

            for (JsonNode node : dataNode) {
                long timestamp = node.get("id").asLong() * 1000L;
                double closePrice = node.get("close").asDouble();
                prices.add(new Price(String.valueOf(timestamp), closePrice));
            }

            return prices;
        } else {
            throw new Exception("Нет данных для символа: " + symbol + ", таймфрейм: " + timeframe);
        }
    }


}