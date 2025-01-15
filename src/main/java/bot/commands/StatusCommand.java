package bot.commands;

import bot.data.TickerRepository;
import bot.data.model.Ticker;
import bot.messages.CommandContext;
import bot.status.Status;

public class StatusCommand extends BaseCommand {
    private  TickerRepository tickerRepository;

    public StatusCommand(TickerRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    @Override
    public String getName() {
        return "!Status"; // Имя команды
    }

    @Override
    public boolean isAvailableInStatus(Status status) {
        return status.getName().equals("ACTIVE"); // Доступность команды в определённом статусе
    }

    @Override
    public void execute(CommandContext context) {
        StringBuilder response = new StringBuilder("Список тикеров:\n");

        // Получаем все тикеры из репозитория
        for (Ticker ticker : tickerRepository.getAllTickers()) {
            response.append("Тикер: ").append(ticker.symbol())
                    .append(", Таймфрейм: ").append(ticker.timeframe())
                    .append(", SMA сигнал: ").append(ticker.SMAsignal())
                    .append(", MACD сигнал: ").append(ticker.MACDsignal())
                    .append("\n");
        }

        // Отправляем сообщение
        context.getMessageSender().sendMessage(response.toString());
    }

    @Override
    public Status getNewStatus() {
        return null; // Если команда не изменяет статус, возвращайте null
    }

}
