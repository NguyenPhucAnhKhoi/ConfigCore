package nguyenkhoi.configcore;

import java.util.ArrayList;
import java.util.HashMap;

import static nguyenkhoi.configcore.Util.matchString;

@SuppressWarnings("unused")
public class DataStorage {
    /**
     * Main storage manager
     */
    private final HashMap<String, Object> data = new HashMap<>();

    private boolean match = true;

    /**
     * Get value from a config path. The path auto match or
     * not dependence in {@link this#setAutoMatch}
     * @param path the config path
     * @return the value store in this path
     */
    protected Object get(String path) {
        if (data.keySet().isEmpty()) return null;
        return data.get(match ? matchString(path, new ArrayList<>(data.keySet())) : path);
    }

    /**
     * Clear all storage data, use in reload
     */
    protected void clear() {
        data.clear();
    }

    /**
     * Put value and path to store in, use in load
     * @param path the path to store
     * @param value the value to store
     */
    protected void put(String path, Object value) {
        data.put(path, value);
    }

    /**
     * Get HashMap represent this data class
     * @return source data hash map
     */
    protected HashMap<String, Object> getData() {
        return data;
    }

    /**
     * Auto match path or not
     * @return true or false
     */
    protected boolean getAutoMatch() {
        return match;
    }

    /**
     * Set the mode of auto match path
     * @param value true or false
     */
    protected void setAutoMatch(boolean value) {
        match = value;
    }
}
