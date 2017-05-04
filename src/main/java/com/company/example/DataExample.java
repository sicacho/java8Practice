package com.company.example;

import com.company.domain.Movie;
import com.company.googledrive.DriveQuickstart;
import com.company.googledrive.Main;
import com.company.googledrive.MovieDTO;
import com.company.googledrive.MovieService;
import com.company.repository.MovieRepository;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tnkhang on 4/28/2017.
 */
public class DataExample {
  public static void main(String[] args) throws IOException {
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
    List<MovieDTO> movieDTOs = new ArrayList<>();
    Drive service = DriveQuickstart.getDriveService();
    getFileList(service,"0BxjPkoVoNqw7ME5pLUtYX09BaXM",null,movieDTOs);

//    MovieRepository movieRepository = (MovieRepository) ctx.getBean("movieRepository");
//    Iterable<Movie> movies = movieRepository.findAll();
//    for (Movie movie : movies) {
//      movie.setUncen(true);
//      movieRepository.save(movie);
//    }
    System.out.println(movieDTOs.size());
    movieDTOs = movieDTOs.stream().map(movieDTO -> {
      movieDTO.image=movieDTO.googleId;
      movieDTO.studio="user upload";
      movieDTO.types= Arrays.asList("drama","mother");
      movieDTO.actors = Arrays.asList("unknown");
      movieDTO.uncen = true;
      movieDTO.description = movieDTO.name;
      return movieDTO;
    }).collect(Collectors.toList());
    movieService.insertMoviesTestData(movieDTOs);
  }

  private static void getFileList(Drive service, String folder, String nextPageToken,List<MovieDTO> movieDTOs) throws IOException {
    FileList fileList = null;
    if(nextPageToken==null) {
       fileList = service.files().list().setQ("'" + folder + "' in parents")
          .setPageSize(100)
          .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
          .execute();
    } else {
      fileList = service.files().list().setQ("'" + folder + "' in parents")
          .setPageSize(100)
          .setPageToken(nextPageToken)
          .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
          .execute();
    }
    List<File> files = fileList.getFiles();
    if (files == null || files.size() == 0) {
      System.out.println("No files found.");
    } else {
      MovieDTO movieDTO = null;
      String fileName = "";
      for (File file : files) {
        if(!file.getName().equals("test")) {
          try {
            System.out.println("Files: " + file.getName() + " | " + file.getId());
            movieDTO = new MovieDTO();
            fileName = file.getName();
            if(fileName.contains(".")) {
              fileName = file.getName().substring(0,file.getName().indexOf("."));
            }
            fileName = fileName.replace("%","");
            movieDTO.name = fileName;
            movieDTO.googleId = file.getId();
            if(file.getVideoMediaMetadata()!=null) {
              movieDTO.isHD = file.getVideoMediaMetadata().getHeight() > 480 ? true : false;
            } else {
              movieDTO.isHD = false;
            }
            movieDTOs.add(movieDTO);
          } catch (Exception e) {
            e.printStackTrace();
          }

        }

      }
    }
    if (fileList.getNextPageToken()!=null && !fileList.getNextPageToken().equals("")) {
      getFileList(service,folder,fileList.getNextPageToken(),movieDTOs);
    }

  }

  private static List<MovieDTO> getDataFromGoogleDrive(String folderId) {
    Drive service = null;
    FileList result = null;
    List<MovieDTO> movieDTOs = new ArrayList<>();
    try {
      service = DriveQuickstart.getDriveService();
      result = service.files().list().setQ("'"+ folderId +"' in parents")
              .setPageSize(200)
              .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
              .execute();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Print the names and IDs for up to 10 files.

    List<File> files = result.getFiles();
    if (files == null || files.size() == 0) {
      System.out.println("No files found.");
    } else {
      MovieDTO movieDTO = null;
      String fileName = "";
      for (File file : files) {
        if(!file.getName().equals("test")) {
          try {
            System.out.println("Files: " + file.getName() + " | " + file.getId());
            movieDTO = new MovieDTO();
            fileName = file.getName();
            if(fileName.contains(".")) {
              fileName = file.getName().substring(0,file.getName().indexOf("."));
            }
            fileName = fileName.replace("%","");
            movieDTO.name = fileName;
            movieDTO.googleId = file.getId();
            movieDTO.isHD = file.getVideoMediaMetadata().getHeight() > 480 ? true : false;
            movieDTOs.add(movieDTO);
          } catch (Exception e) {
            e.printStackTrace();
          }

        }

      }
    }
    System.out.println(movieDTOs.size());
    return  movieDTOs;
  }
}
