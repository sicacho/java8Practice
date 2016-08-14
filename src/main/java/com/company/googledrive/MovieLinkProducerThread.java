package com.company.googledrive;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Administrator on 8/13/2016.
 */
public class MovieLinkProducerThread implements Runnable {

    BlockingQueue<String> movieLinks ;
    Queue<String> pageLinks;
    List<MovieDTO> movieDTOs;
    boolean running = true;

    public MovieLinkProducerThread(BlockingQueue<String> movieLinks, Queue<String> pageLinks, List<MovieDTO> movieDTOs) {
        this.movieLinks = movieLinks;
        this.pageLinks = pageLinks;
        this.movieDTOs = movieDTOs;
    }

    @Override
    public void run() {
        while (running) {
            for (int i = 0; i < pageLinks.size(); i++) {
                String pageLink = pageLinks.poll();
                System.out.println("Producer "+ Thread.currentThread().getName() + " is running with " + pageLink);
                List<MovieDTO> moviesWeHaveInPageLink = MovieParser.getMoviesFromLink(pageLink);
                moviesWeHaveInPageLink.stream().filter(movieDTO -> movieDTOs.contains(movieDTO)).forEach(movieInDB -> {
                    Main.movieCounter++;
                    try {
                        movieLinks.put(movieInDB.link);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
            stop();

        }


    }

    public void stop() {
        if(pageLinks.isEmpty()) {
            System.out.println("Kill producer " + Thread.currentThread().getName() +  " - Total movie = " + Main.movieCounter);
            running = false;
        }
    }
}
