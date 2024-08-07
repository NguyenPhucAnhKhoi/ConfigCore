package nguyenkhoi.configcore;

import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

import static nguyenkhoi.configcore.Util.matchString;

@SuppressWarnings("unused")
public class FileConfig extends YamlConfiguration {
    /**
     * The auto match status True or False
     */
    private boolean autoMatch;

    /**
     * The match mode IGNORE_CASE or NEAREST
     */
    private MatchMode matchMode;

    /**
     * The source yaml file which this class represent
     */
    private YamlConfiguration config;

    /**
     * The source file that represent yaml file
     */
    private  File file;

    /**
     * List of all paths this config has
     */
    private final List<String> paths = new ArrayList<>();

    /**
     * Plugin this config holder to create file
     */
    private final JavaPlugin plugin;

    /**
     * Name of the resources to load from resources
     */
    private final String resourcePath;

    /**
     * Get the Yaml File this class represent
     * @return Yaml File
     */
    public YamlConfiguration getSourceConfig() {
        return config;
    }

    /**
     * Get the paths from this config
     * @return List paths
     */
    public List<String> getPaths() {
        return paths;
    }

    /**
     * Get the file represent this storage
     * @return File
     */
    public File getFile() {
        return new File(plugin.getDataFolder(), resourcePath);
    }

    /**
     * Get the input stream represent this storage
     * @return Input stream
     */
    public InputStream getStream() {
        return this.plugin.getResource(resourcePath);
    }

    /**
     * Get the file path of source file
     * @return File path
     */
    public String getFilePath() {
        return new File(plugin.getDataFolder(), resourcePath).getPath();
    }

    /**
     * Get the match mode for get path
     * @return IGNORE_CASE or NEAREST
     */
    public MatchMode getMatchMode() {
        return matchMode;
    }

    /**
     * Get the status of auto match path
     * @return true or false
     */
    public boolean isAutoMatch() {
        return autoMatch;
    }

    /**
     * Set the match mode of auto match path
     * @param value IGNORE_CASE or NEAREST
     */
    public void setMatchMode(MatchMode value) {
        matchMode = value;
    }

    /**
     * Set the status of auto match path
     * @param value true or false
     */
    public void setAutoMatch(boolean value) {
        autoMatch = value;
    }

    /**
     * Send the colorize message to the console
     * @param message Message will be sent
     */
    private void log(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * Load source file for this storage
     */
    private void loadFile() {
        File folder = plugin.getDataFolder();
        if (!folder.exists()) {
            if (!folder.mkdirs()) log("§cCan not create the config parent folder for plugin &e" + plugin.getName());
        }
        file = new File(folder, resourcePath);
        if (!file.exists()) {
            plugin.saveResource(resourcePath, true);
        }
    }

    /**
     * Construct this class to represent Yaml File
     * @param resourcePath the resource name of file to recreate
     * @param plugin the plugin this config will be link
     * @param autoMatch auto match path for or not
     * @param mode the mode for auto match
     */
    public FileConfig(String resourcePath, JavaPlugin plugin, boolean autoMatch, MatchMode mode) {
        this.plugin = plugin;
        this.resourcePath = resourcePath;
        this.config = new YamlConfiguration();
        this.autoMatch = autoMatch;
        this.matchMode = mode;
        loadFile();
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Set<String> set = Objects.requireNonNull(config.getConfigurationSection("")).getKeys(true);
        paths.addAll(set);
    }

    /**
     * Construct this class to represent Yaml File
     * @param resourcePath the resource name of file to recreate
     * @param plugin the plugin this config will be link
     * @param autoMatch auto match path for or not
     */
    public FileConfig(String resourcePath, JavaPlugin plugin, boolean autoMatch) {
        this(resourcePath, plugin, autoMatch, MatchMode.IGNORE_CASE);
    }

    /**
     * Construct this class to represent Yaml File
     * @param resourcePath the resource name of file to recreate
     * @param plugin the plugin this config will be link
     * @param mode the mode for auto match
     */
    public FileConfig(String resourcePath, JavaPlugin plugin, MatchMode mode) {
        this(resourcePath, plugin, true, mode);
    }

    /**
     * Construct this class to represent Yaml File
     * @param resourcePath the resource name of file to recreate
     * @param plugin the plugin this config will be link
     */
    public FileConfig(String resourcePath, JavaPlugin plugin) {
        this(resourcePath, plugin, true, MatchMode.IGNORE_CASE);
    }

    /**
     * Get object value store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    @Nullable
    public Object get(@NotNull String path, Object def) {
        String finalPath = autoMatch ? matchString(path, paths, matchMode) : path;
        return config.get(finalPath, def);
    }

    /**
     * Reload the config file and this storage
     */
    public void reload() {
        loadFile();
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception ignored) {}
        Set<String> set = Objects.requireNonNull(config.getConfigurationSection("")).getKeys(true);
        paths.clear();
        paths.addAll(set);
    }

    /**
     * Save the config file and this storage
     */
    public void save() {
        try {
            config.save(file);
        } catch (IOException ignored) {}
    }
}
