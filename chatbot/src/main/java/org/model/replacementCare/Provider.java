package org.model.replacementCare;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.model.Address;

public class Provider {

    @JsonProperty("providerName")
    private String providerName;

    @JsonProperty("providerAddress")
    private Address providerAddress;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Address getProviderAddress() {
        return providerAddress;
    }

    public void setProviderAddress(Address providerAddress) {
        this.providerAddress = providerAddress;
    }
}
