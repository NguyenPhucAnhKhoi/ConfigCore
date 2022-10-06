package nguyenkhoi.configcore;

import java.util.List;

public class Util {

    /**
     * Get min edit distance of 2 string
     * @param source the source string
     * @param target the target string
     * @return the min edit distance value
     */
    protected static int getStringDistance(String source, String target) {
        int[][] map = new int[source.length() + 1][target.length() + 1];
        for (int i = 0; i <= source.length(); i++) {
            for (int j = 0; j <= target.length(); j++) {
                if (i == 0) map[0][j] = j;
                else if (j == 0) map[i][0] = i;
                else if (source.charAt(i - 1) != target.charAt(j - 1)) {
                    int min = min(map[i - 1][j - 1], map[i][j - 1], map[i - 1][j]);
                    map[i][j] = min + 1;
                } else {
                    map[i][j] = map[i - 1][j -1];
                }
            }
        }
        return map[source.length()][target.length()];
    }

    /**
     * Get min value of list integer
     * @param numbers the list integer to compare
     * @return the min value in list
     */
    protected static int min(int... numbers) {
        int min = numbers[0];
        for (int i : numbers) {
            if (i < min) min = i;
        }
        return min;
    }

    /**
     * Get the closest string of target if not found the equal
     * @param target the string to match
     * @param strings the list string to find
     * @return the closest string
     */
    protected static String matchString(String target, List<String> strings) {
        boolean e = false;
        boolean i = false;
        int min = getStringDistance(strings.get(0), target);
        String out = strings.get(0);
        for (String s : strings) {
            if (s.equals(target)) {
                out = s;
                e = true;
            }
            else if (s.equalsIgnoreCase(target) && !e) {
                out = s;
                i = true;
            }
            else if (getStringDistance(s, target) < min && !i && !e) {
                min = getStringDistance(s, target);
                out = s;
            }
        }
        return out;
    }
}
