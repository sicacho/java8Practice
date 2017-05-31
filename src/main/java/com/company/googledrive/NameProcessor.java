package com.company.googledrive;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tnkhang on 5/31/2017.
 */

public class NameProcessor {
  public static void main(String[] args) throws IOException {
    Logger LOG = (Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
    LOG.setLevel(Level.INFO);
    ObjectMapper mapper = new ObjectMapper();
    List<MovieDTO> movieDTOS = new ArrayList<>();
    try {
      movieDTOS = mapper.readValue(new File("D:\\xvdDTO.json"), new TypeReference<List<MovieDTO>>(){});
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
    LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
            .withProfiles(languageProfiles)
            .build();
    TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
    movieDTOS.stream().distinct()
            .forEach(movieDTO ->
            {

              String description = movieDTO.description;
              description = description.replaceAll("([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:\\/~+#-]*[\\w@?^=%&\\/~+#-])?","")
                      .replaceAll("[^\\p{L}\\p{Nd}\\s]","");
              TextObject textObject = textObjectFactory.forText(description);
              List<DetectedLanguage> langs = languageDetector.getProbabilities(textObject);
              java.util.Optional<DetectedLanguage> detectedLanguage = langs.stream().sorted((o1, o2) -> o1.compareTo(o2)).findFirst();
              String languages = detectedLanguage.isPresent() ? detectedLanguage.get().getLocale().getLanguage() : "Unknow";
              if(!languages.equals("en")) {
                movieDTO.description  ="Porn clip " + movieDTO.types.stream().filter(s -> (!s.equals("indian") && !s.equals("indonesian") && !s.equals("filipina") && !s.equals("thai") &&!s.equals("indo") &&!s.equals("indonesia") && !s.equals("vietnam") && !s.equals("vietnamese"))).collect(Collectors.joining(" "));
              } else {
                movieDTO.description = description;
              }
              if(movieDTO.description.equals("Porn clip ")) {
                movieDTO.description = "Porn clip homemade asian";
              }
            });
    movieDTOS.forEach(movieDTO -> System.out.println(movieDTO.description));

  }
}
