package pl.isekai.chat.util;

import pl.isekai.chat.ChatApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CensorUtil {
    public static String filter(String message) throws IOException {
        String tempMess = message.replaceAll("\\W", "");

        try (InputStream is = ChatApplication.class.getClassLoader().getResourceAsStream("blacklist.data")) {
            Scanner scanner = new Scanner(is);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                tempMess = tempMess.replaceAll("(?i)" + line, "*".repeat(line.length()));
            }
        }

        Pattern pattern = Pattern.compile("\\W");
        Matcher matcher = pattern.matcher(message);
        int offset = 0;

        while (matcher.find(offset)) {
            offset = matcher.start();
            tempMess = new StringBuilder(tempMess).insert(offset, message.charAt(offset)).toString();

            offset++;
        }

        return tempMess;
    }
}
