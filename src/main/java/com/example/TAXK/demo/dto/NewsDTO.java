package com.example.TAXK.demo.dto;

public class NewsDTO {
    private String category;
    private long datetime;   // timeline (second)
    private String headline;
    private int id;
    private String image;
    private String related;
    private String source;
    private String summary;
    private String url;

    // Getter and Setter
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getDatetime() { return datetime; }
    public void setDatetime(long datetime) { this.datetime = datetime; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getRelated() { return related; }
    public void setRelated(String related) { this.related = related; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}

