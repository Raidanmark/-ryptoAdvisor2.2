package bot.data.websocket.model;

public record InitialResponse(
 String id,
 String status,
 String subbed,
 long ts
) {}
