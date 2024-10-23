package org.springframework.samples.petclinic.app.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "role" })
public enum Role {
    USER,ADMIN
}
