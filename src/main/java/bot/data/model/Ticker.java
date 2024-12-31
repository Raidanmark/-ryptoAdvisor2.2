package bot.data.model;

import java.util.LinkedList;


public record Ticker (
   String symbol,
   String timeframe,
   LinkedList<Double> close,
   long lastTimestamp,// Хранит последний таймстамп обновления
   boolean SMAsignal,
   boolean MACDsignal

) {
    public void addClosePrice(double newPrice, int maxSize) {
        if (close.size() >= maxSize) {
            close.removeFirst(); // Удаляем самое старое значение
        }
        close.addLast(newPrice); // Добавляем новое значение в конец
    }
}

