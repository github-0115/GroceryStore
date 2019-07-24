package uyun.show.server.domain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    private static Logger logger = LoggerFactory.getLogger(RegexUtil.class);

    private static Pattern p = Pattern.compile("\\$\\{[A-Za-z0-9.]+\\}");

    public static boolean findMail(String input) {
//        Pattern p = Pattern.compile("\\$\\{[A-Za-z0-9]+\\.[A-Za-z0-9.@]+\\}");
        Matcher m = p.matcher(input);
        return m.find();
    }

    public static String getMail(String input) {
        return input.substring(input.indexOf("${mail.") + 7, input.indexOf("}"));
    }

    public static String replaceMessage(String message, Map<String, String> param) {
        Matcher m = p.matcher(message);

        Set<String> set = new HashSet<>();
        while (m.find()) {
            set.add(m.group());
        }
        for (String input : set) {
            if (findVar(input)) {
                String var = getVar(input);
                String value = String.valueOf(param.get(var));
                if (value == null) {
                    continue;
                }
                message = message.replace(input, value);
            }
        }

        return message;
    }

    public static boolean findVar(String input) {
        Matcher m = p.matcher(input);
        return m.find();
    }

    public static String getVar(String input) {
        return input.substring(input.indexOf("{") + 1, input.indexOf("}"));
    }


}
