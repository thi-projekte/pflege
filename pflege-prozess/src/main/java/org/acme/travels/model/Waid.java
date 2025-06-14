package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WaId {

    @JsonProperty("waId")
    private String waId;

    

    public void setWaid(String waId) {
        this.waId = waId;
    }

    public String getWaId() {
        return waId;
    }

}
