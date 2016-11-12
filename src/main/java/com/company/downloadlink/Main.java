package com.company.downloadlink;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by KhangTN1 on 8/25/2016.
 */
public class Main {
    public static void main(String[] args) {

        List<LinkCommentDTO> linkCommentDTOs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            linkCommentDTOs = mapper.readValue(new File("D:\\study\\JAVService\\linklist_25082016.json"), new TypeReference<List<LinkCommentDTO>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<LinkCommentDTO> hasLink = new ArrayList<>();
        List<LinkCommentDTO> emptyLink = new ArrayList<>();
        for (int i = 0; i < linkCommentDTOs.size(); i++) {
            LinkCommentDTO comment = linkCommentDTOs.get(i);
            if(comment.rapidgator_net.size() > 0 || comment.uploaded_net.size() >0) {
                hasLink.add(comment);
            } else {
                emptyLink.add(comment);
            }
        }
        System.out.println(hasLink.size());
        System.out.println(emptyLink.size());
        try {
            mapper.writeValue(new File("D:\\study\\JAVService\\listmovie_hasLink_25082016.json"), hasLink);
            mapper.writeValue(new File("D:\\study\\JAVService\\listmovie_emptylink_25082016.json"), emptyLink);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void getLinkFromComment(List<LinkCommentDTO> linkCommentDTOs) {
        Document doc = null;
        List<String> uploaded = null;
        List<String> rapidgator = null;
        linkCommentDTOs = JsonConverter.getData();
        for (int i = 0; i < linkCommentDTOs.size(); i++) {
            LinkCommentDTO linkCommentDTO = linkCommentDTOs.get(i);
            System.out.println("Process : " + linkCommentDTO.linkprimary);
            try {
                doc = Jsoup.connect(linkCommentDTO.linkprimary)
                        .header("Referer","http://www.javlibrary.com/en")
                        .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element comments = doc.getElementById("video_comments");
            String textComments = comments.text();
            Pattern pattern = Pattern.compile("http:\\/\\/uploaded\\.net\\/(folder|file)\\/(.*)\\/(?!\\[)");
            Matcher matcher = pattern.matcher(textComments);
            uploaded = new ArrayList<>();
            while (matcher.find()) {
                uploaded.add(matcher.group());
            }
            Pattern pattern2 = Pattern.compile("http:\\/\\/rapidgator\\.net\\/(folder|file)\\/(.*)\\/(?!\\[)");
            Matcher matcher2 = pattern2.matcher(textComments);
            rapidgator = new ArrayList<>();
            while (matcher2.find()) {
                rapidgator.add(matcher2.group());
            }
            linkCommentDTO.uploaded_net = uploaded;
            linkCommentDTO.rapidgator_net = rapidgator;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("D:\\study\\JAVService\\linklist_25082016.json"), linkCommentDTOs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
