package com.company.thumnailvtt;


import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 12/3/2016.
 */
public class ThumbnailExtractor {

  private final int numColumn = 4;

  private final int breakSecond = 40;

  public void renderVTTFile(String desc,String thumbnailUrl,int height,int width,long videoDuration,int videoWidth,int videoHeight) {
    double numberImage = Math.floor(videoDuration/(breakSecond*1.0));
    double numberRow = numberImage/numColumn;
    int previewHeight = (int) (height/numberRow);
    int previewWidth =(int) width/numColumn;
    String name = "fax-249";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("WEBVTT");
    stringBuilder.append("\n");
    Integer durationStep = 0;
    System.out.println(numberImage);
    int rowCount = 0;
    for(int i = 0 ; i < numberImage; i++) {
      if(i!=0 && i%4==0) {
        rowCount++;
      }
      stringBuilder.append(getTime(durationStep) + "-->" + getTime(durationStep+breakSecond));
      stringBuilder.append("\n");
      stringBuilder.append(name+".jpg?xywh="
          +String.valueOf(getXposition(i,numColumn,previewWidth))+","+getYPosition(rowCount,previewHeight)+","+previewWidth+","+previewHeight);
      stringBuilder.append("\n");
      durationStep = durationStep + breakSecond;
    }
    System.out.println(stringBuilder.toString());
  }

  public int getXposition(int number,int numColumn,int imageWidth) {
    if(number%numColumn==0) {
      return imageWidth*0;
    } else if(number%numColumn==1) {
      return imageWidth*1;
    } else if(number%numColumn==2) {
      return imageWidth*2;
    } else if(number%numColumn==3) {
      return imageWidth*3;
    }
    return 0;
  }

  public int getYPosition(int row,int imageHeight) {
    return row*imageHeight;
  }

  public String getTime(int seconds) {
    String hour = String.valueOf(TimeUnit.SECONDS.toHours(seconds));
    String minute = String.valueOf(TimeUnit.SECONDS.toMinutes(seconds));
    if(TimeUnit.SECONDS.toMinutes(seconds)%60>= 0) {
      minute = String.valueOf(TimeUnit.SECONDS.toMinutes(seconds)%60);
    }
    if(hour.length()==1) {
      hour = "0"+hour;
    }
    if(minute.length()==1) {
      minute = "0"+minute;
    }
    String secondsText = String.valueOf(seconds-((Integer.parseInt(hour)*60+Integer.parseInt(minute))*60));
    if(secondsText.length()==1 ) {
      secondsText = "0"+secondsText;
    }
    return (hour+":"+minute+":"+secondsText);
  }
}
