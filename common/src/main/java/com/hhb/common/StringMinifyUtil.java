package com.hhb.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hhb
 * @date 2020/11/12 18:56
 */
public class StringMinifyUtil {
    /**
     * 去除字符串中的空格、回车、换行符、制表符等
     *
     * @param str
     * @return
     */
    public static String replaceSpecialStr(String str) {
        String repl = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            repl = m.replaceAll("");
        }
        return repl.trim();
    }
}
