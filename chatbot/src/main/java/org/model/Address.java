package org.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {

    @JsonProperty("street")
    private String street;

    @JsonProperty("houseNumber")
    private int houseNumber;

    @JsonProperty("city")
    private String city;

    @JsonProperty("zip")
    private int zip;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }
}
