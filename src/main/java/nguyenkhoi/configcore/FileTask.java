package nguyenkhoi.configcore;

public interface FileTask {
    default void runBefore() {}
    default void runAfter() {}
}
