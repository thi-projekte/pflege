package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Waid {

    @JsonProperty("waid")
    private String waid;

    

    public void setWaid(String waid) {
        this.waid = waid;
    }

    public String getWaid() {
        return waid;
    }

}
