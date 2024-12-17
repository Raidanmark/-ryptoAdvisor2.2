package bot.data;

public class Price {
    private final String timestamp;  // Метка времени, например, "2024-12-01T12:00:00Z"
    private final double closePrice; // Цена закрытия

    // Конструктор
    public Price(String timestamp, double closePrice) {
        this.timestamp = timestamp;
        this.closePrice = closePrice;
    }

    // Геттеры
    public String getTimestamp() {
        return timestamp;
    }

    public double getClosePrice() {
        return closePrice;
    }

    @Override
    public String toString() {
        return "Price{" +
                "timestamp='" + timestamp + '\'' +
                ", closePrice=" + closePrice +
                '}';
    }
}