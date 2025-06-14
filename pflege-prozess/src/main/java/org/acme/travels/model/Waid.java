package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WaId {

    @JsonProperty("waId")
    private String waId;

    public WaId() {
    }
    
    public WaId(String waId) {
        this.waId = waId;
    }

    public void setWaId(String waId) {
        this.waId = waId;
    }

    public String getWaId() {
        return waId;
    }

}
