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
    private final String resourceName;

    /**
     * The path of source file
     */
    private String fileName;

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
        return file;
    }

    /**
     * Get the input stream represent this storage
     * @return Input stream
     */
    public InputStream getStream() {
        return this.plugin.getResource(resourceName);
    }

    /**
     * Get the file path of source file
     * @return File path
     */
    public String getFilePath() {
        return new File(plugin.getDataFolder(), fileName).getPath();
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

    private static String colorize(String input) {
        input = ChatColor.translateAlternateColorCodes('&', input);
        if (getVersion() >= 16) {
            input = translateHexColorCodes(input);
        }
        return input;
    }

    private void log(String message) {
        Bukkit.getConsoleSender().sendMessage(colorize(message));
    }

    private boolean isFileExist() {
        if (file == null) return false;
        else return file.exists();
    }

    private void createFile() {
        if (recreate) {
            File folder = plugin.getDataFolder();
            if (!folder.exists()) {
                if (folder.mkdirs()) log("&cCan not create the config parent folder for plugin &e" + plugin.getName());
            }
            if (isFileExist()) {
                plugin.saveResource(resourceName, false);
            }
        }
    }

    /**
     * Load source file for this storage
     */
    private void loadFile() {
        try {
            File folder = plugin.getDataFolder();
            if (file == null) {
                createFile();
                file = new File(folder, fileName);
            } else {
                createFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
        }
    }

    /**
     * Construct this class to represent Yaml File
     * @param config the bukkit configuration this config will be hold
     * @param file file to load (null for file name load)
     * @param fileName the name of file to load
     * @param resourceName the resource name of file to recreate
     * @param plugin the plugin this config will be link
     * @param recreate create the file in load and reload or not
     */
    public FileConfig(YamlConfiguration config, File file, String fileName, String resourceName, JavaPlugin plugin, boolean recreate) {
        this.plugin = plugin;
        this.resourceName = resourceName;
        this.config = config;
        this.file = file;
        loadFile();
        if (config == null || config.saveToString().isEmpty()) {
            config = new YamlConfiguration();
            try {
                config.load(file);
            } catch (Exception ignored) {}
        }
        Set<String> set = Objects.requireNonNull(config.getConfigurationSection("")).getKeys(true);
        paths = new ArrayList<>(set);
        for (String s : paths) {
            data.put(s, config.get(s));
        }
    }

    /**
     * Construct this class to represent Yaml File
     * Create a FileConfig (with custom auto recreate resource and load from file name)
     * @param fileName the name of file to load
     * @param resourceName the resource name of file to recreate
     * @param plugin the plugin this config will be link
     * @param recreate create the file in load and reload or not
     */
    public FileConfig(String fileName, String resourceName, JavaPlugin plugin, boolean recreate) {
        this(new YamlConfiguration(), null, fileName, resourceName, plugin, recreate);
    }

    /**
     * Construct this class to represent Yaml File
     * Create a FileConfig (with custom auto recreate and load from file name)
     * @param fileName the name of file to load
     * @param plugin the plugin this config will be link
     * @param recreate create the file in load and reload or not
     */
    public FileConfig(String fileName, JavaPlugin plugin, boolean recreate) {
        this(new YamlConfiguration(), null, fileName, fileName, plugin, recreate);
    }

    /**
     * Construct this class to represent Yaml File
     * Create a FileConfig (with custom auto recreate and load from file)
     * @param file the file to load
     * @param recreate create the file in load and reload or not
     */
    public FileConfig(File file, boolean recreate) {
        this(new YamlConfiguration(), file, "", "", null, recreate);
    }

    /**
     * Construct this class to represent Yaml File
     * Create a FileConfig (with custom auto recreate and load from file)
     * @param config the bukkit configuration this config will be hold
     * @param fileName the name of file to load
     * @param resourceName the resource name of file to recreate
     * @param plugin the plugin this config will be link
     * @param recreate create the file in load and reload or not
     */
    public FileConfig(YamlConfiguration config, String fileName, String resourceName, JavaPlugin plugin, boolean recreate) {
        this(config, null, fileName, resourceName, plugin, recreate);
    }

    /**
     * Construct this class to represent Yaml File
     * Create a FileConfig (with custom auto recreate and load from file)
     * @param config the bukkit configuration this config will be hold
     * @param fileName the name of file to load
     * @param plugin the plugin this config will be link
     * @param recreate create the file in load and reload or not
     */
    public FileConfig(YamlConfiguration config, String fileName, JavaPlugin plugin, boolean recreate) {
        this(config, null, fileName, fileName, plugin, recreate);
    }

    /**
     * Construct this class to represent Yaml File
     * Create a FileConfig (with custom auto recreate and load from file)
     * @param config the bukkit configuration this config will be hold
     * @param file the file to load
     * @param recreate create the file in load and reload or not
     */
    private FileConfig(YamlConfiguration config, File file, boolean recreate) {
        this(config, file, "", "", null, recreate);
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
    
    public void set(@NotNull String path, Object value) {
        config.set(path, value);
        data.put(path, value);
    }

    /**
     * Set the comments for this config
     * @param path the path to set
     * @param comments comments string to set
     */
    
    public void setComments(@NotNull String path, List<String> comments) {
        if (getVersion() >= 13) config.setComments(path, comments);
    }

    /**
     * Set the inline comments for this config
     * @param path the path to set
     * @param comments inline comments string to set
     */
    
    public void setInlineComments(@NotNull String path, List<String> comments) {
        if (getVersion() >= 13) config.setComments(path, comments);
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
