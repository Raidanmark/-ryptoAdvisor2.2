package bot.data;

public class Cryptocurrency {
    private final String symbol;
    private final double volume;

    public Cryptocurrency(String symbol, double volume) {
        this.symbol = symbol;
        this.volume = volume;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "Cryptocurrency{" +
                "symbol='" + symbol + '\'' +
                ", volume=" + volume +
                '}';
    }
}
