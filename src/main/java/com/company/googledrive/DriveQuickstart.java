package com.company.googledrive;

import com.company.configuration.RepositoryConfiguration;
import com.company.repository.ActorRepository;
import com.company.repository.MovieRepository;
import com.company.repository.StudioRepository;
import com.company.repository.TypeRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@SpringBootApplication
@ComponentScan
@Import({RepositoryConfiguration.class})
public class DriveQuickstart {
  /**
   * Application name.
   */
  private static final String APPLICATION_NAME =
      "Drive API Java Quickstart";

  /**
   * Directory to store user credentials for this application.
   */
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
  private static final List<String> SCOPES =
      Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);

  static {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Creates an authorized Credential object.
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

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build();
    Credential credential = new AuthorizationCodeInstalledApp(
        flow, new LocalServerReceiver()).authorize("user");
    System.out.println(
        "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
    return credential;
  }

  /**
   * Build and return an authorized Drive client service.
   *
   * @return an authorized Drive client service
   * @throws IOException
   */
  public static Drive getDriveService() throws IOException {
    Credential credential = authorize();
    return new Drive.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  public static void main(String[] args) throws IOException {
    // Build a new authorized API client service.
      Drive service = getDriveService();

    // Print the names and IDs for up to 10 files.


//    updateResolutions(service,args);
      FileList result = getFileList(service,"0Bw7bYe57hsqORk1RODZIVllQT2M");
      backupLink(args, result);

  }

  private static void updateResolutions(Drive service,String[] args) {
    List<String> folders = new ArrayList<>();
    folders.add("0Bw7bYe57hsqOUGhoU3hQUnBFQlU");
    folders.add("0Bw7bYe57hsqOLXFsMTRVdWJvZzg");
    folders.add("0Bw7bYe57hsqOeXlHSVBSQmpMMEE");
    folders.add("0Bw7bYe57hsqOemFFQzFvYVFJTFU");
    folders.add("0Bw7bYe57hsqOVWhTZDROTzRDWEk");
    folders.add("0Bw7bYe57hsqORHYxVURPOWpWZWs");
    folders.add("0Bw7bYe57hsqOb2dXMmQ2TG1QS28");
    folders.add("0Bw7bYe57hsqOQmFKQ1pYOTVFQVk");
    folders.add("0Bw7bYe57hsqOeHpCOWlPOUlwSVk");
    folders.add("0Bw7bYe57hsqObUsxNC03S2FFelE");
    folders.add("0B6iOGhAfgoxVd0t1N05rQ2NGb2c");
    folders.add("0B6iOGhAfgoxVUVZjTlgxMmpuQWM");
    folders.add("0B6iOGhAfgoxVLWo3UG5FV1Jfc0U");
    folders.add("0B6iOGhAfgoxVZ2NHTjhUbEVEZG8");
    folders.add("0B6iOGhAfgoxVVVhPbmFBUkFBeEE");
    folders.add("0B6iOGhAfgoxVVjE4MkJZVnRHdW8");
    folders.add("0B6iOGhAfgoxVckpIVHdtX1U3OUE");
    folders.add("0B6iOGhAfgoxVeko5UkFMQjlFcjg");
    folders.add("0B6iOGhAfgoxVVmg3MlNaeUgweTg");

    final HashMap<String, Integer> videoResolutions = new HashMap<>();
    folders.forEach(s -> {
      FileList result = null;
      try {
        result = getFileList(service, s);
      } catch (IOException e) {
        e.printStackTrace();
      }
      List<File> files = result.getFiles();
      if (files == null || files.size() == 0) {
        System.out.println("No files found.");
      } else {
        files.forEach(file -> {
          if (file.getVideoMediaMetadata() != null) {
            System.out.println(file.getName());
            String fileName = file.getName();
            if (fileName.contains(".")) {
              fileName = file.getName().substring(0, file.getName().indexOf("."));
            }
            videoResolutions.put(fileName, file.getVideoMediaMetadata().getHeight());
          }
        });
      }
    });
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
    movieService.updateResolutions(videoResolutions);
  }

  private static FileList getFileList(Drive service, String folder) throws IOException {
    return service.files().list().setQ("'" + folder + "' in parents")
        .setPageSize(1000)
        .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
        .execute();
  }

  private static void backupLink(String[] args, FileList result) {
    List<File> files = result.getFiles();
    Map<String, String> nameAndLinks = new HashMap<>();
    if (files == null || files.size() == 0) {
      System.out.println("No files found.");
    } else {
      System.out.println("Files:");
      for (File file : files) {
        String fileName = file.getName();
        if (fileName.contains(".")) {
          fileName = file.getName().substring(0, file.getName().indexOf("."));
        }
        nameAndLinks.put(fileName.replace("Copy of ", ""), file.getId());
      }
      nameAndLinks.forEach((s, s2) -> System.out.println(s + " | " + s2));
    }
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
    movieService.updateBackupLink(nameAndLinks);
  }

}