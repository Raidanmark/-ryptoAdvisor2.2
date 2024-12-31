package bot.analytics;

import bot.chatbot.BotListener;
import bot.data.Data;
import bot.data.model.Ticker;

import java.util.List;

public class SMA {
     Data data;
     BotListener listener;

    public SMA(Data data, BotListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void analyzeSMASignal(Ticker ticker) {
        // Получаем список значений закрытия из тикера
        List<Double> closePrices = ticker.close();


        // Рассчитаем текущее SMA
        double currentSMA = calculateSMA(closePrices);

        // Получаем последнее закрытие
        double lastClose = closePrices.get(closePrices.size() - 1);

        // Определяем сигнал
        if (lastClose > currentSMA) {
            if (ticker.SMAsignal() == false) {

                listener.broadcastMessage("Ticker: " + ticker.symbol() + " Timeframe: " + ticker.timeframe() + " SMA signal: BUY");

                Ticker updatedTicker = new Ticker(
                        ticker.symbol(),
                        ticker.timeframe(),
                        ticker.close(),
                        ticker.lastTimestamp(),
                        true,
                        ticker.MACDsignal()
                );
                data.updateTicker(updatedTicker);

                messageTest();

            } else { }
        } else {
            if (ticker.SMAsignal() == true) {

                listener.broadcastMessage("Ticker: " + ticker.symbol() + " Timeframe: " + ticker.timeframe() + " SMA signal: SELL");

                Ticker updatedTicker = new Ticker(
                        ticker.symbol(),
                        ticker.timeframe(),
                        ticker.close(),
                        ticker.lastTimestamp(),
                        false,
                        ticker.MACDsignal()
                );
                data.updateTicker(updatedTicker);
            } else { }
        }
    }

    // Метод для вычисления SMA
    private double calculateSMA(List<Double> prices) {
        double sum = 0;
        for (double price : prices) {
            sum += price;
        }
        return sum / prices.size(); // Возвращаем среднее значение
    }

    public void messageTest() {
        listener.broadcastMessage("Тest SMA signal: BUY");
    }
}
