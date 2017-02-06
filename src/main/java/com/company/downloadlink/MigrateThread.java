package com.company.downloadlink;

import com.company.domain.Movie;

/**
 * Created by Administrator on 1/19/2017.
 */
public class MigrateThread implements Runnable{

  Movie movie;

  public MigrateThread(Movie movie) {
    this.movie = movie;
  }

  @Override
  public void run() {

  }
}
