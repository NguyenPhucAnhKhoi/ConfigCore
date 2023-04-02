package nguyenkhoi.configcore;

import org.apache.commons.io.FileUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

import static nguyenkhoi.configcore.Util.getVersion;
import static org.bukkit.util.NumberConversions.*;

@SuppressWarnings("unused")
public class FileConfig {
    /**
     * The hash map store all data of this class
     */
    private final DataStorage data = new DataStorage();

    /**
     * The source yaml file which this class represent
     */
    private YamlConfiguration config;

    /**
     * The source file that represent yaml file
     */
    private  File file;

    /**
     * The path of source file
     */
    private final String filePath;

    /**
     * List of all paths this config has
     */
    private List<String> paths;

    /**
     * ByteArrayOutputStream to create file
     */
    private final ByteArrayOutputStream outstream;

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
        return new ByteArrayInputStream(this.outstream.toByteArray());
    }

    /**
     * Get the file path of source file
     * @return File path
     */
    
    public String getFilePath() {
        return filePath;
    }

    /**
     * Get the mode of auto match path
     * @return true or false
     */

    public boolean isAutoMatch() {
        return data.getAutoMatch();
    }

    /**
     * Set the mode of auto match path
     * @param value true or false
     */

    public void setAutoMatch(boolean value) {
        data.setAutoMatch(value);
    }

    /**
     * Load source file for this storage
     */
    private void loadFile() {
        try {
            file = new File(filePath);
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) throw new IOException("Can not create the config parent file");
                if (!file.createNewFile()) throw new IOException("Can not create the config file");
                else FileUtils.copyInputStreamToFile(getStream(), file);
            } else {
                if (!file.exists()) {
                    if (!file.createNewFile()) throw new IOException("Can not create the config file");
                    else FileUtils.copyInputStreamToFile(getStream(), file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
        }
    }

    /**
     * Construct this class to represent Yaml File
     * @param filePath the path to create file
     * @param stream input stream to create file if it
     *               not exists
     */
    public FileConfig(String filePath, InputStream stream) {
        ByteArrayOutputStream outstream1;
        this.filePath = filePath;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) > -1 ) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
            outstream1 = byteArrayOutputStream;
        } catch (Exception e) {
            outstream1 = null;
        }
        this.outstream = outstream1;
        loadFile();
        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (Exception ignored) {
            file = null;
        }
        Set<String> set = Objects.requireNonNull(config.getConfigurationSection("")).getKeys(true);
        paths = new ArrayList<>(set);
        for (String s : paths) {
            data.put(s, config.get(s));
        }
    }

    /**
     * Construct this class to represent Yaml File
     * @param filePath the path to create file
     */
    public FileConfig(String filePath) {
        this(filePath, new InputStream() {
            @Override
            public int read() {
                return -1;
            }
        });
    }

    /**
     * Construct this class to represent Yaml File
     * @param file the file to create config
     */
    public FileConfig(File file) {
        this(file.getPath());
    }

    /**
     * Check if this config contains a path or not
     * @param path the path to check
     * @return contain or not
     */
    
    public boolean contains(String path) {
        return paths.contains(path);
    }

    /**
     * Create the Configuration Section of this config
     * @param path the path to the section
     * @return the section
     */
    
    public ConfigurationSection createConfigurationSection(String path) {
        return config.createSection(path);
    }

    /**
     * Get the Configuration Section of this config
     * @param path the path to the section
     * @return the section
     */

    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }

    /**
     * Get object value store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    @Nullable
    
    public Object get(String path, Object def) {
        return data.get(path) != null ? data.get(path) : def;
    }

    /**
     * Get object value store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public Object get(String path) {
        return data.get(path);
    }

    /**
     * Get boolean value store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public boolean getBoolean(String path, Boolean def) {
        Object var = get(path);
        return (var instanceof Boolean) ? (Boolean) var : def;
    }

    /**
     * Get boolean value store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    /**
     * Get boolean list value store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<Boolean> getBooleanList(String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }
        List<Boolean> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean) object);
            } else if (object instanceof String) {
                if (Boolean.TRUE.toString().equals(object)) {
                    result.add(true);
                } else if (Boolean.FALSE.toString().equals(object)) {
                    result.add(false);
                }
            }
        }
        return result;
    }

    /**
     * Get byte list value store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<Byte> getByteList(String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }
        List<Byte> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Byte) {
                result.add((Byte) object);
            } else if (object instanceof String) {
                try {
                    result.add(Byte.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((byte) ((Character) object).charValue());
            } else if (object instanceof Number) {
                result.add(((Number) object).byteValue());
            }
        }
        return result;
    }

    /**
     * Get character list value store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<Character> getCharList(String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }
        List<Character> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Character) {
                result.add((Character) object);
            } else if (object instanceof String) {
                String str = (String) object;

                if (str.length() == 1) {
                    result.add(str.charAt(0));
                }
            } else if (object instanceof Number) {
                result.add((char) ((Number) object).intValue());
            }
        }
        return result;
    }

    /**
     * Get color value store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public Color getColor(String path, Color def) {
        return data.get(path) instanceof Color ? (Color) data.get(path) : def;
    }

    /**
     * Get color value store in this path
     * @param path the path of value
     * @return value store in path
     */
    @Nullable
    
    public Color getColor(String path) {
        return getColor(path, null);
    }

    /**
     * Get comments store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<String> getComments(String path) {
        if (getVersion() >= 13) return config.getComments(path);
        return new ArrayList<>();
    }

    /**
     * Get double value store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public Double getDouble(String path, double def) {
        return data.get(path) instanceof Double ? (Double) data.get(path) : def;
    }

    /**
     * Get double value store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public Double getDouble(String path) {
        Object def = get(path);
        return getDouble(path, (def instanceof Number) ? toDouble(def) : 0);
    }

    /**
     * Get double list value store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<Double> getDoubleList(String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }
        List<Double> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Double) {
                result.add((Double) object);
            } else if (object instanceof String) {
                try {
                    result.add(Double.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((double) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).doubleValue());
            }
        }

        return result;
    }

    /**
     * Get float list value store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<Float> getFloatList(String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }
        List<Float> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Float) {
                result.add((Float) object);
            } else if (object instanceof String) {
                try {
                    result.add(Float.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((float) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).floatValue());
            }
        }
        return result;
    }

    /**
     * Get inline comments store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<String> getInLineComments(String path) {
        if (getVersion() >= 13) return config.getInlineComments(path);
        return new ArrayList<>();
    }

    /**
     * Get integer value store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public int getInt(String path, int def) {
        return data.get(path) instanceof Integer ? (Integer) data.get(path) : def;
    }

    /**
     * Get integer store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public int getInt(String path) {
        Object def = get(path);
        return getInt(path, (def instanceof Number) ? toInt(def) : 0);
    }

    /**
     * Get integer list store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<Integer> getIntegerList(String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }
        List<Integer> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Integer) {
                result.add((Integer) object);
            } else if (object instanceof String) {
                try {
                    result.add(Integer.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((int) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).intValue());
            }
        }
        return result;
    }

    /**
     * Get item stack value store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public ItemStack getItemStack(String path, ItemStack def) {
        return data.get(path) instanceof ItemStack ? (ItemStack) data.get(path) : def;
    }


    /**
     * Get item stack store in this path
     * @param path the path of value
     * @return value store in path
     */
    @Nullable
    
    public ItemStack getItemStack(String path) {
        return getItemStack(path, null);
    }

    /**
     * Get list store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public List<?> getList(String path, List<?> def) {
        return data.get(path) instanceof List<?> ? (List<?>) data.get(path) : def;
    }

    /**
     * Get list store in this path
     * @param path the path of value
     * @return value store in path
     */
    public List<?> getList(String path) {
        return getList(path, new ArrayList<>());
    }

    /**
     * Get location store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public Location getLocation(String path, Location def) {
        return data.get(path) instanceof Location ? (Location) data.get(path) : def;
    }

    /**
     * Get location in this path
     * @param path the path of value
     * @return value store in path
     */
    @Nullable
    
    public Location getLocation(String path) {
        return getLocation(path, null);
    }

    /**
     * Get long store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public Long getLong(String path, long def) {
        return data.get(path) instanceof Location ? (Long) data.get(path) : def;
    }

    /**
     * Get long in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public Long getLong(String path) {
        Object def = get(path);
        return getLong(path, (def instanceof Number) ? toLong(def) : 0);
    }

    /**
     * Get long list store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<Long> getLongList(String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }
        List<Long> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Long) {
                result.add((Long) object);
            } else if (object instanceof String) {
                try {
                    result.add(Long.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((long) (Character) object);
            } else if (object instanceof Number) {
                result.add(((Number) object).longValue());
            }
        }
        return result;
    }

    /**
     * Get map list store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<Map<?, ?>> getMapList(String path) {
        List<?> list = getList(path);
        List<Map<?, ?>> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        for (Object object : list) {
            if (object instanceof Map) {
                result.add((Map<?, ?>) object);
            }
        }
        return result;
    }

    /**
     * Get request object store in this path
     * @param path the path of value
     * @return value store in path
     */
    @Nullable
    
    public <T> T getObject(String path, Class<T> type) {
        Object out = get(path);
        return (type.isInstance(out)) ? type.cast(out) : null;
    }

    /**
     * Get offline player store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
        return data.get(path) instanceof OfflinePlayer ? (OfflinePlayer) data.get(path) : def;
    }

    /**
     * Get offline player store in this path
     * @param path the path of value
     * @return value store in path
     */
    @Nullable
    
    public OfflinePlayer getOfflinePlayer(String path) {
        return getOfflinePlayer(path, null);
    }

    /**
     * Get short list store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<Short> getShortList(String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }
        List<Short> result = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Short) {
                result.add((Short) object);
            } else if (object instanceof String) {
                try {
                    result.add(Short.valueOf((String) object));
                } catch (Exception ignored) {}
            } else if (object instanceof Character) {
                result.add((short) ((Character) object).charValue());
            } else if (object instanceof Number) {
                result.add(((Number) object).shortValue());
            }
        }
        return result;
    }

    /**
     * Get string store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public String getString(String path, String def) {
        return data.get(path) != null ? data.get(path).toString() : def;
    }

    /**
     * Get string store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public String getString(String path) {
        Object def = get(path);
        return getString(path, def != null ? def.toString() : "");
    }

    /**
     * Get string list store in this path
     * @param path the path of value
     * @return value store in path
     */
    
    public List<String> getStringList(String path) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof String) {
                result.add(String.valueOf(object));
            }
        }
        return result;
    }

    /**
     * Get values map store all values in this config
     * @return map store all values
     */
    
    public Map<String, Object> getValues() {
        Map<String, Object> map = new HashMap<>();
        for (String s : getPaths()) {
            map.put(s, get(s));
        }
        return map;
    }

    /**
     * Get vector store in this path
     * @param path the path of value
     * @param def the default value
     * @return value store in path or default value if it wasn't store
     */
    public Vector getVector(String path, Vector def) {
        return data.get(path) instanceof Vector ? (Vector) data.get(path) : def;
    }

    /**
     * Get vector store in this path
     * @param path the path of value
     * @return value store in path
     */
    @Nullable
    
    public Vector getVector(String path) {
        return getVector(path, null);
    }

    /**
     * Check if value instance of boolean or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isBoolean(String path) {
        return data.get(path) instanceof Boolean;
    }

    /**
     * Check if value instance of color or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isColor(String path) {
        return data.get(path) instanceof Color;
    }

    /**
     * Check if value instance of double or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isDouble(String path) {
        return data.get(path) instanceof Double;
    }

    /**
     * Check if value instance of int or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isInt(String path) {
        return data.get(path) instanceof Integer;
    }

    /**
     * Check if value instance of item stack or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isItemStack(String path) {
        return data.get(path) instanceof ItemStack;
    }

    /**
     * Check if value instance of list or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isList(String path) {
        return data.get(path) instanceof List;
    }

    /**
     * Check if value instance of location or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isLocation(String path) {
        return data.get(path) instanceof Location;
    }

    /**
     * Check if value instance of long or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isLong(String path) {
        return data.get(path) instanceof Long;
    }

    /**
     * Check if value instance of offline player or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isOfflinePlayer(String path) {
        return data.get(path) instanceof OfflinePlayer;
    }

    /**
     * Check if value was set or not
     * @param path the path of value
     * @return value was set or not
     */
    
    public boolean isSet(String path) {
        return data.get(path) != null;
    }

    /**
     * Check if value instance of string or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isString(String path) {
        return data.get(path) instanceof String;
    }

    /**
     * Check if value instance of vector or not
     * @param path the path of value
     * @return value instance of type or not
     */
    
    public boolean isVector(String path) {
        return data.get(path) instanceof Vector;
    }

    /**
     * Store the value in this path
     * @param path the path to save
     * @param value the value to save
     */
    
    public void set(String path, Object value) {
        config.set(path, value);
        data.put(path, value);
    }

    /**
     * Set the comments for this config
     * @param path the path to set
     * @param comments comments string to set
     */
    
    public void setComments(String path, List<String> comments) {
        if (getVersion() >= 13) config.setComments(path, comments);
    }

    /**
     * Set the inline comments for this config
     * @param path the path to set
     * @param comments inline comments string to set
     */
    
    public void setInlineComments(String path, List<String> comments) {
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
