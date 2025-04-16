package org.model.replacementCare;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.langchain4j.model.output.structured.Description;
import org.model.Address;

public class Provider {

    @JsonProperty("providerName")
    @Description("Name of the professional care provider. Required if isProfessional is true.")
    private String providerName;

    @JsonProperty("providerAddress")
    @Description("Address of the professional care provider. Required if isProfessional is true.")
    private Address providerAddress;

    public boolean isproviderNameValid() {
        return providerName != null && !providerName.trim().isEmpty();
    }


    public boolean isValid(){
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
