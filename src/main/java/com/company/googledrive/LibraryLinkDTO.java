package com.company.googledrive;

/**
 * Created by khang on 3/26/2017.
 */
public class LibraryLinkDTO {
  public long id;
  public String link;
  public String code;

  public LibraryLinkDTO(long id, String link, String code) {
    this.id = id;
    this.link = link;
    this.code = code;
  }

  public LibraryLinkDTO() {
  }
}
