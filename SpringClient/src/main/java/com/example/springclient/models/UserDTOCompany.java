package com.example.springclient.models;

import lombok.Data;

@Data
public class UserDTOCompany {
    private Long id;

    private String username;

    private String email;

    private String password;

    private String firstname;

    private String lastname;

    private String role;

    private String keycloak_id;

    private String company_id;

    private String name;

    private String shortName;

    private String ogrn;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username=" + username +
                ", email=" + email +
                ", password=" + password +
                ", firstName=" + firstname +
                ", lastName=" + lastname +
                ", role=" + role +
                ", keycloackId=" + keycloak_id +
                '}' +
                "Company{" +
                "companyId=" + company_id +
                ", name=" + name +
                ", shortName=" + shortName +
                ", ogrn=" + ogrn +
                '}';
    }
}
