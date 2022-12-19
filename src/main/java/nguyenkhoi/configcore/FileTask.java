package nguyenkhoi.configcore;

import java.io.File;

public interface FileTask {
    default void runBefore() {}
    default void runAfter(File file) {}
    default void runFinal() {}
}
