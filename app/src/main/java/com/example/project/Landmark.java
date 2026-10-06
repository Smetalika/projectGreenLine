package com.example.project;

public class Landmark {
    private String name;
    private String description;
    private int imageResource;
    private String url;
    private double latitude;
    private double longitude;
    private String mapUrl;

    public Landmark(String name, String description, String url, int imageResource,
                    double latitude, double longitude) {
        this(name, description, url, null, imageResource, latitude, longitude);
    }

    public Landmark(String name, String description, String url, String mapUrl, int imageResource,
                    double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.mapUrl = mapUrl;
        this.imageResource = imageResource;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getMapUrl() { return mapUrl; }
    public String getName(){
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getUrl(){
        return url;
    }
    public int getImageResource(){
        return  imageResource;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
}
