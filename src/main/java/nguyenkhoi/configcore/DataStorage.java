package nguyenkhoi.configcore;

import java.util.ArrayList;
import java.util.HashMap;

import static nguyenkhoi.configcore.Util.matchString;

public class DataStorage {
    private final HashMap<String, Object> data = new HashMap<>();

    public Object get(String path) {
        return data.get(matchString(path, new ArrayList<>(data.keySet())));
    }

    public void clear() {
        data.clear();
    }

    public void put(String path, Object value) {
        data.put(path, value);
    }

    public HashMap<String, Object> getData() {
        return data;
    }
}
