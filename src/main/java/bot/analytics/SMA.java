package bot.analytics;

import bot.chatbot.BotListener;
import bot.data.Data;
import bot.data.model.Ticker;

import java.util.List;

public class SMA implements Analyzer{
     private final Data data;
     private final BotListener listener;

    public SMA(Data data, BotListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public void analyze(Ticker ticker) {
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

                System.out.println("BUY");

                Ticker updatedTicker = new Ticker(
                        ticker.symbol(),
                        ticker.timeframe(),
                        ticker.close(),
                        ticker.lastTimestamp(),
                        true,
                        ticker.MACDsignal()
                );
                data.updateTicker(updatedTicker);



            } else { System.out.println("BUY");}
        } else {
            if (ticker.SMAsignal() == true) {

                listener.broadcastMessage("Ticker: " + ticker.symbol() + " Timeframe: " + ticker.timeframe() + " SMA signal: SELL");

                System.out.println("SELL");

                Ticker updatedTicker = new Ticker(
                        ticker.symbol(),
                        ticker.timeframe(),
                        ticker.close(),
                        ticker.lastTimestamp(),
                        false,
                        ticker.MACDsignal()
                );
                data.updateTicker(updatedTicker);
            } else {  System.out.println("SELL");}
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
}
