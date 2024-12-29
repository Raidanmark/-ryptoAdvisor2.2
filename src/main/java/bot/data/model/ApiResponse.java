package bot.data.model;


public record ApiResponse<T> (
    String status,
    T data,
    long ts,
    int full,
    String ch
){}