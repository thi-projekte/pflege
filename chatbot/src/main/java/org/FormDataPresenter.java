package org;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormDataPresenter {
    public String present(FormData formData) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(formData);
    }
}
