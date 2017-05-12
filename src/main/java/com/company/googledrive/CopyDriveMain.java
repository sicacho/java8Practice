package com.company.googledrive;

import com.company.configuration.RepositoryConfiguration;
import com.company.domain.Movie;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tnkhang on 1/13/2017.
 */
@SpringBootApplication
@ComponentScan
@Import({RepositoryConfiguration.class})
public class CopyDriveMain {
  private static final java.io.File DATA_STORE_DIR = new java.io.File(
      System.getProperty("user.home"), ".credentials/drive-java-quickstart");

  /**
   * Global instance of the {@link FileDataStoreFactory}.
   */
  private static FileDataStoreFactory DATA_STORE_FACTORY;

  /**
   * Global instance of the JSON factory.
   */
  private static final JsonFactory JSON_FACTORY =
      JacksonFactory.getDefaultInstance();

  /**
   * Global instance of the HTTP transport.
   */
  private static HttpTransport HTTP_TRANSPORT;

  /**
   * Global instance of the scopes required by this quickstart.
   * <p>
   * If modifying these scopes, delete your previously saved credentials
   * at ~/.credentials/drive-java-quickstart
   */
  private static final Set<String> SCOPES = DriveScopes.all();
  private static final List<String> FullScope = new ArrayList<>();

  static {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }
  public static void getFileList(List<String> ids, Drive service, String folder, String nextPageToken) throws IOException {
    FileList fileList = null;
    if (nextPageToken!=null && !nextPageToken.equals("")) {
      fileList = service.files().list().setQ("'" + folder + "' in parents")
          .setPageSize(100)
          .setPageToken(nextPageToken)
          .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
          .execute();
    } else {
      fileList = service.files().list().setQ("'" + folder + "' in parents")
          .setPageSize(100)
          .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
          .execute();
    }
    fileList.getFiles().stream().forEach(file -> ids.add(file.getId()));
    if(fileList.getNextPageToken()!=null) {
      getFileList(ids,service,folder,fileList.getNextPageToken());
    }
  }
  /**
   * Creates an authorized Caredential object.
   *
   * @return an authorized Credential object.
   * @throws IOException
   */
  public static Credential authorize() throws IOException {
    // Load client secrets.
    InputStream in =
        DriveQuickstart.class.getResourceAsStream("/client_secret.json");
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    FullScope.add(DriveScopes.DRIVE);
    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, FullScope)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline").setApprovalPrompt("auto")
            .build();
    Credential credential = new AuthorizationCodeInstalledApp(
        flow, new LocalServerReceiver()).authorize("834766686975");
    System.out.println(
        "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
    return credential;
  }

  public static void main(String[] args) {
    int flag = 0;
    try {
      Credential credential = authorize();
      Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
          .setHttpRequestInitializer(credential).setApplicationName("834766686975").build();
      ApplicationContext ctx = null;
      ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
      MovieService movieService = (MovieService) ctx.getBean("movieService");
      Permission permission = new Permission();
      permission.setType("anyone");
      permission.setRole("reader");
      List<String> ids = new ArrayList<>();
          getFileList(ids, service, "0B6iOGhAfgoxVYjRkYmpmT2FpUlU", "");
      for (int i = 1; i < 213; i++) {
        flag = i;
        Iterable<Movie> movies = movieService.getMovies(i, 10, null);
        for (Movie movie : movies) {
          if (movie.getCopyLinkOriginal() != null && ids.contains(movie.getId())==false) {
            String id = movie.getCopyLinkOriginal();
            System.out.println(id);
            System.out.println(movie.getName());
            File file = new File();
            file.setParents(Arrays.asList("0B6iOGhAfgoxVYjRkYmpmT2FpUlU"));
            file.setMimeType("video/mp4");
            try {
              File fileCopy = service.files().copy(id, file).execute();
              service.permissions().create(fileCopy.getId(), permission).execute();
            } catch (GoogleJsonResponseException json) {
              if(json.getDetails().getCode()==404) {
                movie.setCopyLinkOriginal(null);
                movieService.updateMovie(movie);
              }
            }
//            movie.setCopyLinkOriginal(fileCopy.getId()+"|"+"orginal");
//            movieService.updateMovie(movie);
            System.out.println(movie.getCode());

          }
        }
      }

    } catch (Exception e) {
      System.out.println(flag);
      e.printStackTrace();
    }
  }
}
