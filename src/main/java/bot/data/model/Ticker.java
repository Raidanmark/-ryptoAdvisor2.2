package bot.data.model;

import java.util.List;

public record Ticker (
   String symbol,
   String timeframe,
   List<Double> close
) {}
