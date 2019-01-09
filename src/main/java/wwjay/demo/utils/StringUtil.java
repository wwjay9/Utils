package wwjay.demo.utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/**
 * 字符串工具
 *
 * @author wwj
 */
public class StringUtil {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = UPPER.toLowerCase(Locale.ROOT);
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUM = UPPER + LOWER + DIGITS;
    private static final Random RANDOM = new SecureRandom();
    private static final char[] SYMBOLS = ALPHANUM.toCharArray();

    private StringUtil() {
    }

    /**
     * 生成一个12位随机字符串
     */
    public static String randomString() {
        return randomString(12);
    }

    /**
     * 生成一个指定长度的随机字符串
     *
     * @param length 随机字符串的长度
     */
    public static String randomString(int length) {
        char[] buf = new char[length];
        for (int i = 0; i < length; ++i) {
            buf[i] = SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
        }
        return new String(buf);
    }

    /**
     * 生成一个没有-分隔符的uuid字符串
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 提取一个字符串的前面部分
     *
     * @param str  原始字符串
     * @param size 提取的字符串大小，如size比str的长度小，则返回str
     * @return 提取的前面部分字符串
     */
    public static String substringBegin(String str, int size) {
        if (str.length() < size) {
            return str;
        }
        return str.substring(0, size);
    }
}