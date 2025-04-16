package org;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormDataPresenter {
    public String present(FormData formData) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(formData);
        } catch (Exception e) {
            return "Could not convert formData to JSON: " + e.getMessage();
        }

    }
}
