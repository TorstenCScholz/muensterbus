package de.doaschdn.muensterbus.de.doaschdn.muensterbus.util;


/**
 * Created by Torsten on 14.11.2015.
 */
public class StringUtil {
    public static String getLongestCommonPrefix(String str1, String str2) {
        int maxLength = Math.min(str1.length(), str2.length());

        for (int i = 0; i < maxLength; ++i) {
            if (str1.charAt(i) != str2.charAt(i)) {
                return str1.substring(0, i);
            }
        }

        return str1;
    }
}
