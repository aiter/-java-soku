package com.youku.search.drama;

import java.io.Serializable;
import java.util.List;

public class Drama implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Type DEFAULT_TYPE = Type.DRAMA;

    public enum Type {
        DRAMA, // 剧集
        ZONGYI, // 综艺
    };

    private Type type = DEFAULT_TYPE;

    private int id;
    private String name;
    private List<String> alias;
    private List<Version> versions;

    private String version_key_show;// 默认显示的版本id
    private int realVersionCount;// 真实的版本数量，如果剧集过多可能分解导致数量变化

    public void decRealVersionCount() {
        if (realVersionCount > 0) {
            realVersionCount--;
        }
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

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

    public int getVersionCount() {
        return versions == null ? 0 : versions.size();
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

    public void setShowVersion(String version_key) {
        this.version_key_show = version_key;
    }

    public String getShowVersion() {
        return version_key_show;
    }

    public int getRealVersionCount() {
        return realVersionCount;
    }

    public void setRealVersionCount(int realVersionCount) {
        this.realVersionCount = realVersionCount;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("type不能为null");
        }
        this.type = type;
    }

}
