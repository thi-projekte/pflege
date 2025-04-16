package org.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;

public class Address {

    @JsonProperty("street")
    @Description("The Street of the address. This is a required field.")
    private String street;

    @JsonProperty("houseNumber")
    @Description("The house number of the street. This is a required field.")
    private int houseNumber;

    @JsonProperty("city")
    @Description("City name. This is a required field.")
    private String city;

    @JsonProperty("zip")
    @Description("Postal code (PLZ) of the address. Should be a valid 5-digit code. This is a required filed.")
    private int zip;

    public boolean isStreetValid() {
        return street != null && !street.trim().isEmpty();
    }

    public boolean isHouseNumberValid() {
        return houseNumber != 0;
    }

    public boolean isZipValid() {
        return zip != 0 && String.valueOf(zip).matches("^\\d{5}$");
    }

    public boolean isCityValid() {
        return city != null && !city.trim().isEmpty();
    }

    public boolean isValid() {
        return isStreetValid() && isHouseNumberValid() && isZipValid() && isCityValid() ;
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

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }
}
