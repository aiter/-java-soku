package com.youku.search.drama;

import java.io.Serializable;

public class EpisodeVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int fk_episode_id;
    private int video_id;

    private String vidEncoded;
    private String sourceName;
    private String logo;
    private float seconds;

    public static EpisodeVideo mock(Episode episode) {
        EpisodeVideo video = new EpisodeVideo();
        video.setId(-1);
        video.setFk_episode_id(episode.getId());
        video.setVideo_id(episode.getVid());
        video.setVidEncoded(episode.getVidEncoded());
        video.setSourceName(episode.getSourceName());
        video.setLogo(episode.getLogo());
        video.setSeconds(episode.getSeconds());
        return video;
    }

    public int getFk_episode_id() {
        return fk_episode_id;
    }

    public void setFk_episode_id(int fk_episode_id) {
        this.fk_episode_id = fk_episode_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getVidEncoded() {
        return vidEncoded;
    }

    public void setVidEncoded(String vidEncoded) {
        this.vidEncoded = vidEncoded;
    }

    public int getVideo_id() {
        return video_id;
    }

    public void setVideo_id(int video_id) {
        this.video_id = video_id;
    }

}
