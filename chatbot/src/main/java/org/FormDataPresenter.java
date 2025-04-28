package org;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FormDataPresenter {

    @Inject
    ObjectMapper mapper;

    public String present(FormData formData) {
        try {
            return mapper.writeValueAsString(formData);
        } catch (Exception e) {
            return "Could not convert formData to JSON: " + e.getMessage();
        }
    }
}
