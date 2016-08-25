package com.company.downloadlink;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by KhangTN1 on 8/25/2016.
 */
public class Main {
    public static void main(String[] args) {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.javlibrary.com/en/videocomments.php?mode=1&v=javlilrm7e")
                    .header("Referer","http://www.javlibrary.com/en")
                    .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element comments = doc.getElementById("video_comments");
        String textComments = comments.text();
        Pattern pattern = Pattern.compile("http:\\/\\/uploaded\\.net\\/(folder|file)\\/(.*)\\/(?!\\[)");
        Matcher matcher = pattern.matcher(textComments);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
        Pattern pattern2 = Pattern.compile("http:\\/\\/rapidgator\\.net\\/(folder|file)\\/(.*)\\/(?!\\[)");
        Matcher matcher2 = pattern2.matcher(textComments);
        while (matcher2.find()) {
            System.out.println(matcher2.group());
        }
    }
}
