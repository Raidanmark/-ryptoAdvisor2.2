package bot.data.api.model;

public record ApiResponse<T> (
    String status,
    T data,
    long ts,
    int full
){}