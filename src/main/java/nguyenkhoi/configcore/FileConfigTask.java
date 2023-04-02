package nguyenkhoi.configcore;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@SuppressWarnings("unused")
public interface FileConfigTask {
    /**
     * Run this method before load file
     */
    default void runBefore() {}

    /**
     * Run this method after load file
     */
    default void runAfter() {}

    /**
     * @see FileConfigTask#runAfter()
     * @param file File was load
     */
    default void runAfter(File file) {}

    /**
     * Run this method if file is not exist
     * and will be created by system
     */
    default void runIfCreate() {}

    /**
     * @see FileConfigTask#runIfCreate()
     * @param file File was created
     */
    default void runIfCreate(File file) {}

    /**
     * Run this method when instance of api file
     * already created
     */
    default void runFinal() {}

    /**
     * @see FileConfigTask#runAfter()
     * @param file File was loaded while
     * create instance
     */
    default void runFinal(File file) {}

    /**
     * @see FileConfigTask#runAfter()
     * @param config Bukkit config file was
     * created while create instance
     */
    default void runFinal(FileConfiguration config) {}

    /**
     * @see FileConfigTask#runAfter()
     * @param config Instance of this api config
     * file was created while create instance
     */
    default void runFinal(FileConfig config) {}
}
