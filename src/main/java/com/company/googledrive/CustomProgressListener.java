package com.company.googledrive;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.*;
import java.util.Arrays;

/**
 * Created by Administrator on 1/25/2017.
 */
class CustomProgressListener implements MediaHttpDownloaderProgressListener {
  private String newname;
  private Drive service;
  private OutputStream outputStream;

  public CustomProgressListener(String newname, Drive service,OutputStream outputStream) {
    this.newname = newname;
    this.service = service;
    this.outputStream = outputStream;
  }

  Permission permission = new Permission();

  public void progressChanged(MediaHttpDownloader downloader) {
    switch (downloader.getDownloadState()) {
      case MEDIA_IN_PROGRESS:
        break;
      case MEDIA_COMPLETE:
        System.out.println("Download is complete!");
        try {

          File fileMetadata = new File();
          fileMetadata.setName(newname);
          fileMetadata.setParents(Arrays.asList("0B28pDkSFd-VZdllaanJmRnhQLUk"));
          java.io.File mediaFile = new java.io.File(java.io.File.separator + System.getProperty("user.home")+ java.io.File.separator +"downloadtest"+ java.io.File.separator +newname);
          InputStreamContent mediaContent =
              new InputStreamContent("video/mp4",
                  new BufferedInputStream(new FileInputStream(mediaFile),16000000));
          File file = service.files().create(fileMetadata, mediaContent).execute();
          System.out.println(file.getId());
          permission.setType("anyone");
          permission.setRole("reader");
          service.permissions().create(file.getId(), permission).execute();
          mediaContent.getInputStream().close();
          outputStream.close();
          System.out.println("Delete file : " + mediaFile.delete());
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
  }
}