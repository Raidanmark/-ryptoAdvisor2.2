package bot.analytics;

import bot.chatbot.BotListener;
import bot.data.Data;
import bot.data.model.Ticker;

import java.util.List;

import static bot.data.DataConfig.*;

public class MACD {
    Data data;
    BotListener listener;

    public MACD(Data data, BotListener listener) {
        this.data = data;
        this.listener = listener;
    }
    // Метод для анализа сигнала MACD
    public void analyzeMACDSignal(Ticker ticker) {
        // Получаем список цен закрытия
        List<Double> closePrices = ticker.close();

        // Вычисляем MACD и сигнальную линию
        double macdValue = calculateMACD(closePrices);
        double signalLineValue = calculateSignalLine(closePrices);

        // Определяем сигнал
        if (macdValue > signalLineValue) {

            if (ticker.MACDsignal() == false) {

                listener.broadcastMessage("Ticker: " + ticker.symbol() + " Timeframe: " + ticker.timeframe() + " MACD signal: BUY");

                Ticker updatedTicker = new Ticker(
                        ticker.symbol(),
                        ticker.timeframe(),
                        ticker.close(),
                        ticker.lastTimestamp(),
                        ticker.SMAsignal(),
                        true
                );
                data.updateTicker(updatedTicker);

                messageMacdTest();
            } else { }
        } else {

            if (ticker.MACDsignal() == true) {

                listener.broadcastMessage("Ticker: " + ticker.symbol() + " Timeframe: " + ticker.timeframe() + " MACD signal: SELL");

                Ticker updatedTicker = new Ticker(
                    ticker.symbol(),
                    ticker.timeframe(),
                    ticker.close(),
                    ticker.lastTimestamp(),
                    ticker.SMAsignal(),
                    false
                );
                data.updateTicker(updatedTicker);
            } else { }


        }
    }

    // Метод для вычисления MACD
    private double calculateMACD(List<Double> prices) {
        double shortEMA = calculateEMA(prices, getMacdShortPeriod()); // EMA за короткий период (12)
        double longEMA = calculateEMA(prices, getMacdLongPeriod());  // EMA за длинный период (26)
        return shortEMA - longEMA;
    }

    // Метод для вычисления сигнальной линии (9-периодная EMA от MACD)
    private double calculateSignalLine(List<Double> prices) {
        List<Double> macdValues = calculateMACDValues(prices);
        return calculateEMA(macdValues, getMacdSignalPeriod());
    }

    // Метод для вычисления всех значений MACD
    private List<Double> calculateMACDValues(List<Double> prices) {
        return prices.stream()
                .map(price -> calculateMACD(prices))
                .toList();
    }

    // Метод для вычисления EMA
    private double calculateEMA(List<Double> prices, int period) {
        double smoothing = 2.0 / (period + 1);
        double ema = prices.get(0); // Начальное значение EMA (первое значение)
        for (int i = 1; i < prices.size(); i++) {
            ema = prices.get(i) * smoothing + ema * (1 - smoothing);
        }
        return ema;
    }

    public void messageMacdTest() {
        listener.broadcastMessage("Тest MACD signal: BUY");
    }
}
