package com.tnowad.week5_1;

public class Country {

    private String name;
    private String capital;
    private String region;

    public Country(String capital, String name, String region) {
        this.capital = capital;
        this.name = name;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return name + " - Capital: " + capital + ", Region: " + region;
    }
}
