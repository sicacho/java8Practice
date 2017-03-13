package com.company.googledrive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tnkhang on 2/7/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListFileDTO {
  public Result result;

  @JsonIgnoreProperties(ignoreUnknown = true)
  private class Result {
    public ArrayList<File> files;

    public Result() {
    }

  }
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class File {
    public String name;

    public File() {

    }

    public File(String name) {
      this.name = name;
    }
  }

  public List<String> getNames() {
    return this.result.files.stream().map(file -> file.name.replace(".mp4","")).collect(Collectors.toList());
  }


}
