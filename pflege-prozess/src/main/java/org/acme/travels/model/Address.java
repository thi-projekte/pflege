package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Address {

    @JsonProperty("street")
   
    private String street;

    @JsonProperty("houseNumber")
   
    private int houseNumber;

    @JsonProperty("city")
   
    private String city;

    @JsonProperty("zip")
   
    private String zip;

    public boolean isStreetValid() {
        return street != null && !street.trim().isEmpty();
    }

    public boolean isHouseNumberValid() {
        return houseNumber != 0;
    }

    public boolean isZipValid() {
        return zip != null && zip.matches("^\\d{5}$");
    }

    public boolean isCityValid() {
        return city != null && !city.trim().isEmpty();
    }

    public boolean isValid() {
        return isStreetValid() && isHouseNumberValid() && isZipValid() && isCityValid();
    }

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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
