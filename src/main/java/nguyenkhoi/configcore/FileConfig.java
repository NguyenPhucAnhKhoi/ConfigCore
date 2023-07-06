package nguyenkhoi.configcore;

import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nguyenkhoi.configcore.Util.getVersion;
import static org.bukkit.ChatColor.COLOR_CHAR;

@SuppressWarnings("unused")
public class FileConfig extends YamlConfiguration {
    /**
     * The hash map store all data of this class
     */
    private final DataStorage data = new DataStorage();

    /**
     * The recreate file mode in this class
     */
    private boolean recreate;

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
    private List<String> paths;

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
     * Get the Data Storage of this class
     * @return HashMap storage data
     */
    public HashMap<String, Object> getDataStorage() {
        return data.getData();
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
     * Get the mode of auto match path
     * @return true or false
     */
    public boolean isAutoMatch() {
        return data.getAutoMatch();
    }

    /**
     * Get the mode of automatic file creator
     * @return true or false
     */
    public boolean isAutoCreate() {
        return recreate;
    }

    /**
     * Set the mode of auto match path
     * @param value true or false
     */
    public void setAutoMatch(boolean value) {
        data.setAutoMatch(value);
    }

    /**
     * Set the mode of auto create
     * @param value true or false
     */
    public void setAutoCreate(boolean value) {
        this.recreate = value;
    }


    /**
     * Translate normal string to minecraft hex color message (For 1.16+)
     * @param message Message to translate
     * @return Translated hex color message
     */
    private static String translateHexColorCodes(String message) {
        final Pattern hexPattern = Pattern.compile("&#" + "([A-Fa-f0-9]{6})" + "");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR
                    + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));

        }
        return matcher.appendTail(buffer).toString();
    }

    /**
     * Translate normal string to minecraft colorize message
     * @param input The input string will be translated
     * @return Translated colorize message
     */
    private static String colorize(String input) {
        input = ChatColor.translateAlternateColorCodes('&', input);
        if (getVersion() >= 16) {
            input = translateHexColorCodes(input);
        }
        return input;
    }

    /**
     * Send the colorize message to the console
     * @param message Message will be sent
     */
    private void log(String message) {
        Bukkit.getConsoleSender().sendMessage(colorize(message));
    }

    /**
     * Load source file for this storage
     */
    private void loadFile(boolean reload) {
        File folder = plugin.getDataFolder();
        if (!folder.exists()) {
            if (!folder.mkdirs()) log("&cCan not create the config parent folder for plugin &e" + plugin.getName());
        }
        File file = new File(folder, resourcePath);
        if (!file.exists()) {
            plugin.saveResource(resourcePath, true);
        }
    }

    /**
     * Construct this class to represent Yaml File
     * @param resourcePath the resource name of file to recreate
     * @param plugin the plugin this config will be link
     * @param recreate create the file in load and reload or not
     */
    public FileConfig(String resourcePath, JavaPlugin plugin, boolean recreate) {
        this.plugin = plugin;
        this.resourcePath = resourcePath;
        this.config = new YamlConfiguration();
        loadFile(false);
        try {
            config.load(file);
        } catch (Exception ignored) {}
        Set<String> set = Objects.requireNonNull(config.getConfigurationSection("")).getKeys(true);
        paths = new ArrayList<>(set);
        for (String s : paths) {
            data.put(s, config.get(s));
        }
    }

    /**
     * Get object value store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    @Nullable
    public Object get(@NotNull String path, Object def) {
        return data.get(path) != null ? data.get(path) : def;
    }

    /**
     * Get object value store in this path
     * @param path the path of value
     * @return value store in path
     */
    @Nullable
    public Object get(@NotNull String path) {
        return data.get(path);
    }

    /**
     * Set object value store in this path
     * @param path the path of value
     * @param value value to store in path
     */
    public void set(@NotNull String path, Object value) {
        config.set(path, value);
        data.put(path, value);
    }

    /**
     * Reload the config file and this storage
     */
    public void reload() {
        loadFile(true);
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception ignored) {}
        Set<String> set = Objects.requireNonNull(config.getConfigurationSection("")).getKeys(true);
        paths = new ArrayList<>(set);
        data.clear();
        for (String s : paths) {
            data.put(s, config.get(s));
        }
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
