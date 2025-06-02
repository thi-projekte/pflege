package org.acme.travels.model.replacementcare;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.acme.travels.model.Address;


public class Provider {

    @JsonProperty("providerName")

    private String providerName;

    @JsonProperty("providerAddress")
  
    private Address providerAddress;

    public boolean isproviderNameValid() {
        return providerName != null && !providerName.trim().isEmpty();
    }

    public boolean isValid() {
        return isproviderNameValid() &&
                providerAddress != null && providerAddress.isValid();

    }

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
