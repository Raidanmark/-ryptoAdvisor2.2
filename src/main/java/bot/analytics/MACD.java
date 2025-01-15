package bot.analytics;

import bot.chatbot.BotListener;
import bot.data.Data;
import bot.data.model.Ticker;

import java.util.ArrayList;
import java.util.List;

import static bot.data.DataConfig.*;

public class MACD implements Analyzer {
    private final Data data;
    private final BotListener listener;

    public MACD(Data data, BotListener listener) {
        this.data = data;
        this.listener = listener;
    }

    // Метод для анализа сигнала MACD
    @Override
    public void analyze(Ticker ticker) {
        // Получаем список цен закрытия
        List<Double> closePrices = ticker.close();

        // Вычисляем MACD и сигнальную линию
        double macdValue = calculateMACD(closePrices);
        double signalLineValue = calculateSignalLine(closePrices);

        // Определяем сигнал
        if (macdValue > signalLineValue) {

            if (ticker.MACDsignal() == false) {

                listener.broadcastMessage("Ticker: " + ticker.symbol() + " Timeframe: " + ticker.timeframe() + " MACD signal: BUY");

                System.out.println("BUY");

                Ticker updatedTicker = new Ticker(
                        ticker.symbol(),
                        ticker.timeframe(),
                        ticker.close(),
                        ticker.lastTimestamp(),
                        ticker.SMAsignal(),
                        true
                );
                data.updateTicker(updatedTicker);

            } else { System.out.println("BUY"); }
        } else {

            if (ticker.MACDsignal() == true) {

                listener.broadcastMessage("Ticker: " + ticker.symbol() + " Timeframe: " + ticker.timeframe() + " MACD signal: SELL");

                System.out.println("SELL");

                Ticker updatedTicker = new Ticker(
                    ticker.symbol(),
                    ticker.timeframe(),
                    ticker.close(),
                    ticker.lastTimestamp(),
                    ticker.SMAsignal(),
                    false
                );
                data.updateTicker(updatedTicker);
            } else {System.out.println("SELL"); }


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
        List<Double> shortEMA = calculateEMAValues(prices, getMacdShortPeriod()); // EMA короткого периода
        List<Double> longEMA = calculateEMAValues(prices, getMacdLongPeriod());  // EMA длинного периода
        List<Double> macdValues = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {
            macdValues.add(shortEMA.get(i) - longEMA.get(i)); // Разница короткого и длинного EMA
        }
        return macdValues;
    }

    // Метод для вычисления всех значений EMA
    private List<Double> calculateEMAValues(List<Double> prices, int period) {
        List<Double> emaValues = new ArrayList<>();
        double smoothing = 2.0 / (period + 1);
        double ema = prices.get(0); // Начальное значение EMA
        emaValues.add(ema);

        for (int i = 1; i < prices.size(); i++) {
            ema = prices.get(i) * smoothing + ema * (1 - smoothing);
            emaValues.add(ema);
        }
        return emaValues;
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


}
