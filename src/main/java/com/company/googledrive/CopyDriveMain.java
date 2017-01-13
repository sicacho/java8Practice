package com.company.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by tnkhang on 1/13/2017.
 */
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

  /**
   * Creates an authorized Credential object.
   *
   * @return an authorized Credential object.
   * @throws IOException
   */
  public static Credential authorize() throws IOException {
    // Load client secrets.
    InputStream in =
            DriveQuickstart.class.getResourceAsStream("/client_secret_2.json");
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
            flow, new LocalServerReceiver()).authorize("432161060989");
    System.out.println(
            "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
    return credential;
  }

  public static void main(String[] args) {
    try {
      Credential credential = authorize();
      Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
              .setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
      File file = new File();
      file.setParents(Arrays.asList("0B28pDkSFd-VZaTZFREZhTmZDRWs"));
      file.setMimeType("image/png");
      service.files().copy("0B3YJQgQ5nWc3VkJJN0NUV2tHcU0",file).execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
