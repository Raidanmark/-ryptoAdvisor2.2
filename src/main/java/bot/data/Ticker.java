package bot.data;

import java.util.List;

public record Ticker (
   String symbol,
   String timeframe,
   List<Double> close
) {}
