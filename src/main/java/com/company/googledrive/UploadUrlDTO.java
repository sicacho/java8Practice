package com.company.googledrive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by tnkhang on 1/19/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadUrlDTO {

  public Result result;

  public String getUrlUpload() {
    return result.url;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private class Result {
    String url;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public Result() {
    }
  }

  public UploadUrlDTO() {
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }
}
