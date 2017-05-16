package com.company.googledrive;

import com.company.domain.Movie;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 1/27/2017.
 */
public class UpdatePreviewLink {
  public static void main(String[] args) throws IOException, GeneralSecurityException {
    GoogleDriveService googleDriveService = new GoogleDriveService();
    Credential credential = googleDriveService.authorize();
    Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
        .setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
//    for (int i = 1; i < 119; i++) {
//      Iterable<Movie> movies = movieService.getMovies(i,null,null);
//      movies.forEach(movie -> {
//        movie.setShow(true);
//        movieService.updateMovie(movie);
//      });
//    }
    String folder = "0B28pDkSFd-VZdllaanJmRnhQLUk";
    FileList fileList = getFileList(service, folder, "");
    Map<Long, String> name_id = new HashMap<>();
    fileList.getFiles().stream().forEach(file1 -> {
      try {
        File file = new File();
        file.setViewersCanCopyContent(false);
        service.files().update(file1.getId(),file).execute();
//        name_id.put(Long.parseLong(file1.getName()), file1.getId());
      } catch (NumberFormatException num) {
        System.out.println(file1.getId());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
//    while (fileList.getNextPageToken() != null && fileList.getNextPageToken().isEmpty()) {
//      fileList = getFileList(service, folder, fileList.getNextPageToken());
//      fileList.getFiles().stream().forEach(file -> {
//        try {
//          File fileUpdate = new File();
//          fileUpdate.setViewersCanCopyContent(false);
//          System.out.println("Update : " + file.getName());
//          service.files().update(file.getId(),fileUpdate).execute();
////          name_id.put(Long.parseLong(file.getName()), file.getId());
//        } catch (NumberFormatException num) {
//          System.out.println(file.getId());
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      });
//    }
//    name_id.forEach((s, s2) -> {
//      if (s != null) {
//        System.out.println(s);
//        Movie movie = movieService.getMovie(s);
//        if (movie.getCopyLinkOriginal() == null) {
//          movie.setCopyLinkOriginal(s2);
//          movieService.updateMovie(movie);
//        }
//      }
//    });

  }

  private static FileList getFileList(Drive service, String folder, String nextPageToken) throws IOException {
    if (!nextPageToken.equals("")) {
      return service.files().list().setQ("'" + folder + "' in parents")
          .setPageSize(1000)
          .setPageToken(nextPageToken)
          .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
          .execute();
    }
    return service.files().list().setQ("'" + folder + "' in parents")
        .setPageSize(1000)
        .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
        .execute();
  }
}
