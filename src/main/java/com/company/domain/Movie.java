package com.company.domain;

import org.neo4j.ogm.annotation.*;

import java.util.List;

/**
 * Created by khangtnse60992 on 2/21/2016.
 */
@NodeEntity(label = "Movie")
public class Movie {
    @GraphId
    private Long id;

    @Property(name = "code")
    private String code;

    @Property(name = "name")
    private String name;

    @Property(name = "views")
    private Integer views;

    @Property(name = "rate")
    private Integer rate;

    @Property(name = "video")
    private String[] urlVideos;

    @Property(name = "image")
    private String urlImage;

    @Property(name = "create_date")
    private Long createDate;

    @Relationship(type = "BELONG_TO")
    private Studio studio;

    @Property(name = "detail")
    private String detail;

    @Property(name = "isHD")
    private Boolean isHD;

    @Relationship(type = "ACTED_IN",direction = Relationship.INCOMING)
    private List<Actor> actors;

    @Relationship(type = "HAS")
    private List<Type> types;

    private String seoName;

    @Property(name = "copyOriginalLink")
    private String copyLinkOriginal;

    @Property(name = "copy360Link")
    private String copy360Link;

    @Property(name = "copy480Link")
    private String copy480Link;

    @Property(name = "copy720Link")
    private String copy720Link;

    @Property(name = "copy1080Link")
    private String copy1080Link;

//    @Transient
//    private int value;
//
//    public Movie() {
//    }
//
//    public Movie(int value) {
//        this.value = value;
//        views = 300;
//        createDate = System.currentTimeMillis();
//        detail = "This is detail for Movie " + value;
//        urlVideos = new String[] {"https://drive.google.com/open?id=0B3YJQgQ5nWc3VkJJN0NUV2tHcU0"};
//        urlImage = "http://dougboy.smugmug.com/Automotive/Dans-S2000/i-kKrHRTj/0/L/IMG4642-L.jpg";
//    }
//
//    public void setDataRandom(Iterable<Studio> studios,Iterable<Type> types,Iterable<Actor> actors) {
//        if(this.types==null) {
//            this.types = new ArrayList<>();
//        }
//
//        if(this.actors==null) {
//            this.actors = new ArrayList<>();
//        }
//
//        int count = 0 ;
//        Random ran = new Random();
//        int ranStudio = ran.nextInt(4) + 1;
//        Iterator<Studio> studioIterator = studios.iterator();
//        while(studioIterator.hasNext()) {
//            count++;
//            if(count==ranStudio) {
//                studio = studioIterator.next();
//            } else {
//                studioIterator.next();
//            }
//        }
//        this.name = "Name of " + studio.getName() + " " + value;
//        this.code = studio.getName() + " " + value;
//        count=0;
//        int ranNumberType = ran.nextInt(5) + 1;
//        List<Integer> ranTypes = new ArrayList<Integer>();
//        IntStream.range(1,ranNumberType+1).boxed().forEachOrdered(t -> ranTypes.add(ran.nextInt(38) + 1));
//        int ranNumberActor = ran.nextInt(4) + 1;
//        List<Integer> ranActors = new ArrayList<Integer>();
//        IntStream.range(1,ranNumberActor+1).boxed().forEachOrdered(t -> ranActors.add(ran.nextInt(37) + 1));
//
//        Iterator<Type> typeIterator = types.iterator();
//        while (typeIterator.hasNext()) {
//            count++;
//            if(ranActors.contains(count)) {
//                this.types.add(typeIterator.next());
//            } else {
//                typeIterator.next();
//            }
//        }
//
//        count =0;
//
//        Iterator<Actor> actorIterator = actors.iterator();
//        while (actorIterator.hasNext()) {
//            count++;
//            if(ranActors.contains(count)) {
//                this.actors.add(actorIterator.next());
//            } else {
//                actorIterator.next();
//            }
//        }
//
//
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String[] getUrlVideos() {
        return urlVideos;
    }

    public void setUrlVideos(String[] urlVideos) {
        this.urlVideos = urlVideos;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Studio getStudio() {
        return studio;
    }

    public void setStudio(Studio studio) {
        this.studio = studio;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getSeoName() {
        seoName = name.replace(" ","-");
        return seoName;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }

    public boolean isHD() {
        if(isHD==null) {
            isHD=false;
        }
        return isHD;
    }

    public void setHD(boolean HD) {
        isHD = HD;
    }

    public String getCopyLinkOriginal() {
        return copyLinkOriginal;
    }

    public void setCopyLinkOriginal(String copyLinkOriginal) {
        this.copyLinkOriginal = copyLinkOriginal;
    }

    public String getCopy360Link() {
        return copy360Link;
    }

    public void setCopy360Link(String copy360Link) {
        this.copy360Link = copy360Link;
    }

    public String getCopy480Link() {
        return copy480Link;
    }

    public void setCopy480Link(String copy480Link) {
        this.copy480Link = copy480Link;
    }

    public String getCopy720Link() {
        return copy720Link;
    }

    public void setCopy720Link(String copy720Link) {
        this.copy720Link = copy720Link;
    }

    public String getCopy1080Link() {
        return copy1080Link;
    }

    public void setCopy1080Link(String copy1080Link) {
        this.copy1080Link = copy1080Link;
    }
}
