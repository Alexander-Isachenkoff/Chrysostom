package chrysostom.util;

import java.awt.*;
import java.util.Random;

public class Colors
{
    public static String toHex(Color color) {
        String r = Integer.toHexString(color.getRed());
        String g = Integer.toHexString(color.getGreen());
        String b = Integer.toHexString(color.getBlue());
        if (r.length() < 2) {
            r += "0";
        }
        if (g.length() < 2) {
            g += "0";
        }
        if (b.length() < 2) {
            b += "0";
        }
        return "#" + r+g+b;
    }
    
    public static String generateHexColorFor(String string) {
        int seed = string.hashCode();
        Random rnd = new Random(seed);
        return toHex(Color.getHSBColor(rnd.nextFloat(), 0.5f, 0.9f));
    }
}
