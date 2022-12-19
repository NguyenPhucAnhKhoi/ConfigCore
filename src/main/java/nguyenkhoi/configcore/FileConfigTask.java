package nguyenkhoi.configcore;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@SuppressWarnings("unused")
public interface FileConfigTask {
    default void runBefore() {}
    default void runAfter() {}
    default void runAfter(File file) {}
    default void runFinal() {}
    default void runFinal(File file) {}
    default void runFinal(FileConfiguration config) {}
    default void runFinal(FileConfig config) {}
}
