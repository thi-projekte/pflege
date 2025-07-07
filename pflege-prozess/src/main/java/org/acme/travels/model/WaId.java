package org.acme.travels.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WaId {

    @JsonProperty("waId")
    private String whatsappId;

    public WaId() {}

    public WaId(String whatsappId) {
        this.whatsappId = whatsappId;
    }

    public void setWhatsappId(String whatsappId) {
        this.whatsappId = whatsappId;
    }

    public String getWhatsappId() {
        return whatsappId;
    }

}
