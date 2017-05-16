package com.company.googledrive;

import com.company.domain.Movie;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khang on 5/12/2017.
 */
public class DeletePermissionGoogleDrive {
  public static void main(String[] args) {
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
    for(int i=1;i<=213;i++) {
      Iterable<Movie> movies = movieService.getMovies(i,10,"");
      List<String> googleIDs = new ArrayList<>();
      movies.forEach(movie -> googleIDs.add(movie.getUrlVideos()[0].replace("https://drive.google.com/open?id=","")));
      deletePermission(googleIDs);
    }

  }

  private static void deletePermission(List<String> googleIDs) {
    Drive service = null;
    try {
      Permission newPermission = new Permission();
      newPermission.setRole("writer");
      newPermission.setType("user");
      newPermission.setEmailAddress("admin@javdrama.com");
      service = DriveQuickstart.getDriveService();
      for(String id : googleIDs) {
        File file = service.files().get(id).setFields("permissions").execute();
        List<Permission> permissions = file.getPermissions();
        if(permissions
            .stream()
            .anyMatch(permission -> permission.getId().equals("anyoneWithLink"))) {
          service.permissions().delete(id, "anyoneWithLink").execute();
        }
        if(permissions
            .stream()
            .noneMatch(permission -> permission.getEmailAddress().equals("admin@javdrama.com"))) {
          System.out.println("Create Admin permission : " + id);
          service.permissions().create(id, newPermission).execute();
        }
//        if(permissions.stream().findFirst())
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
