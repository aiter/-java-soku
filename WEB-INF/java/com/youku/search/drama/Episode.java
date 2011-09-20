package com.youku.search.drama;

import java.io.Serializable;
import java.util.List;

public class Episode implements Serializable {

    private static final long serialVersionUID = 1L;

    private Version version;

    private int id;
    private String name;
    private int order;
    private String sourceName;
    private int vid;
    private String vidEncoded;
    private String logo;
    private float seconds;
    private int islock;
    private List<EpisodeVideo> videos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public String getVidEncoded() {
        return vidEncoded;
    }

    public void setVidEncoded(String vidEncoded) {
        this.vidEncoded = vidEncoded;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public float getSeconds() {
        return seconds;
    }

    public void setSeconds(float seconds) {
        this.seconds = seconds;
    }

    public List<EpisodeVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<EpisodeVideo> videos) {
        this.videos = videos;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public int getIslock() {
        return islock;
    }

    public void setIslock(int islock) {
        this.islock = islock;
    }

}
