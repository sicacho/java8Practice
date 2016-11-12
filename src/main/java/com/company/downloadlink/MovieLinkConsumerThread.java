package com.company.downloadlink;

import com.company.googledrive.*;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Administrator on 9/13/2016.
 */
public class MovieLinkConsumerThread implements Runnable {

    BlockingQueue<String> movieLinks ;
    BlockingQueue<String> pageLinks;
    List<LinkCommentDTO> commentDTOs;
    boolean running = true;

    public MovieLinkConsumerThread(BlockingQueue<String> movieLinks,
                                   List<LinkCommentDTO> commentDTOs,
                                   BlockingQueue<String> pageLinks
                                   ) {
        this.movieLinks = movieLinks;
        this.commentDTOs = commentDTOs;
        this.pageLinks = pageLinks;
    }

    @Override
    public void run() {
        while (running) {
                    String link  = movieLinks.poll();
                    if(link!=null) {
                        System.out.println("Thread "+ Thread.currentThread().getName() +" is running with Parse Link = " + link);
                        System.out.println("MovieLinks size : " + movieLinks.size());
                        LinkCommentDTO linkCommentDTO = MovieParser.getLinkCommentDTO(link);
                        if(linkCommentDTO==null) {
                            try {
                                movieLinks.put(link);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println(linkCommentDTO.code_video);
                            commentDTOs.add(linkCommentDTO);
                        }
                    }
            stop();
        }
    }

    public void stop() {
        System.out.println("Number movie (current) : " + commentDTOs.size());
        System.out.println("Number movieCounter (current) : " + MovieAnalyser.movieCounter);
        System.out.println("Page link is empty : " + pageLinks.isEmpty());
        if(pageLinks.isEmpty()) {
            if(commentDTOs.size() >= MovieAnalyser.movieCounter){
                System.out.println("Kill consumer " + Thread.currentThread().getName() +  " - Total movie = " + commentDTOs.size());
                running = false;

            }
        }

    }
}
