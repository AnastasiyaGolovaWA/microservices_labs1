package com.example.springclient.controllers;

import com.example.springclient.config.ConfigurationService;
import com.example.springclient.models.UserDTO;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


@RequestMapping(value = "/users")
@RestController
public class UserController {
    @Autowired
    ConfigurationService configurationService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ResponseEntity<String> getAdmin() {
        return ResponseEntity.ok("Hello Admin");
    }

    @PostMapping(path = "/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {

        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(configurationService.getAuthServerUrl())
                .grantType(OAuth2Constants.PASSWORD).realm("master").clientId("admin-cli")
                .username("admin").password("admin")
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        keycloak.tokenManager().getAccessToken();

        UserDTO userDTO1 = new UserDTO();
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());

        RealmResource realmResource = keycloak.realm(configurationService.getRealm());
        UsersResource usersResource = realmResource.users();

        Response response = usersResource.create(user);

        userDTO1.setFirstname(user.getUsername());
        userDTO1.setLastname(user.getLastName());
        userDTO1.setEmail(user.getEmail());
        userDTO1.setId(user.getId());
        userDTO1.setUsername(user.getUsername());
        userDTO1.setPassword(userDTO.getPassword());
        userDTO1.setRole(userDTO.getRole());

        if (response.getStatus() == 201) {

            String userId = CreatedResponseUtil.getCreatedId(response);

            log.info("Created userId {}", userId);
            System.out.println("Created userId {}" + userId);

            userDTO1.setKeycloak_id(userId);


            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(userDTO.getPassword());

            UserResource userResource = usersResource.get(userId);

            userResource.resetPassword(passwordCred);

            /*RoleMappingResource roleMappingResource = realmResource
                    .users()
                    .get(user.getId())
                    .roles();

            List<RoleRepresentation> clientRolesToAdd = new ArrayList<RoleRepresentation>();
            RoleRepresentation clientRole_ = realmResource
                    .clients()
                    .get("keycloak-app")
                    .roles()
                    .get("user")
                    .toRepresentation();
            clientRolesToAdd.add(clientRole_);

            roleMappingResource.clientLevel("keycloak-app").add(clientRolesToAdd);*/
            
        }
        System.out.println(userDTO1);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping(path = "/signin")
    public ResponseEntity<?> signin(@RequestBody UserDTO userDTO) {

        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", configurationService.getClientSecret());

        Configuration configuration =
                new Configuration(configurationService.getAuthServerUrl(), configurationService.getRealm(), configurationService.getClientId(), clientCredentials, null);
        AuthzClient authzClient = AuthzClient.create(configuration);

        AccessTokenResponse response =
                authzClient.obtainAccessToken(userDTO.getUsername(), userDTO.getPassword());

        return ResponseEntity.ok(response);
    }
}