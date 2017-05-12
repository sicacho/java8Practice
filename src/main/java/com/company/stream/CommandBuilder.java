package com.company.stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by khang on 3/27/2017.
 */
public class CommandBuilder {
  static String command = "";
  public static void main(String[] args) {
    String fileName = "C:\\lines.txt";
    //read file into stream, try-with-resources
    try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
      stream.forEach(s -> {command = command + "unrar e '"+s+"' && rm ";});

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
