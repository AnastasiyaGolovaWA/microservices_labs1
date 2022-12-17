package com.example.springclient.controllers;

import com.example.springclient.AppController;
import com.example.springclient.config.ConfigurationService;
import com.example.springclient.models.UserDTOCompany;
import com.example.springclient.models.UserDTO;
import com.example.springclient.service.KeycloakService;
import com.example.springclient.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.*;
import java.util.stream.Stream;

@RequestMapping(value = "/users")
@RestController
public class UserController {
    private UserService userService;

    private AppController appController;

    private KeycloakService keycloakService;

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @Autowired
    public void setKeycloakService(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @DeleteMapping(path = "/delete/{userId}")
    public String deleteUser(@PathVariable("userId") String userId) {
        Keycloak keycloak = keycloakService.getToken();
        RealmResource realmResource = keycloak.realm(configurationService.getRealm());
        UsersResource usersResource = realmResource.users();
        usersResource.get(userId).remove();
        return "User Deleted Successfully.";
    }

    @GetMapping(path = "/find/{userId}")
    public UserRepresentation findUserById(@PathVariable("userId") String userId) {
        Keycloak keycloak = keycloakService.getToken();
        RealmResource realmResource = keycloak.realm(configurationService.getRealm());
        UsersResource usersResource = realmResource.users();
        UserRepresentation user = usersResource.get(userId).toRepresentation();
        return user;
    }

    @GetMapping(path = "/findAllUsers/{litter}")
    public Stream<UserRepresentation> findAllUsers(@PathVariable("litter") String litter) {
        Keycloak keycloak = keycloakService.getToken();
        RealmResource realmResource = keycloak.realm(configurationService.getRealm());
        List<UserRepresentation> list = realmResource.users().list();
        return list.stream().filter(x -> x.getUsername().startsWith(litter));
    }

    @GetMapping(path = "/hello")
    public String getHello() {
        return "CCCMMCMCMCMCMCM";
    }

    @PutMapping(path = "/update/{userId}")
    public String updateUser(@PathVariable("userId") String userId, @RequestBody UserDTO userDTO) throws IllegalAccessException {
        Keycloak keycloak = keycloakService.getToken();
        RealmResource realmResource = keycloak.realm(configurationService.getRealm());
        UsersResource usersResource = realmResource.users();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setFirstName(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());

        UserResource userResource1 = keycloak
                .realm(configurationService.getRealm())
                .users()
                .get(userId);
        if (!Objects.equals(userDTO.getPassword(), null)) {
            keycloakService.setPassword(userDTO, usersResource, userId);
        }

        UserRepresentation obj = usersResource.get(userId).toRepresentation();

        final UserDTO model = userService.findByUsername(obj.getUsername());
        System.out.println(model);
        if (userDTO.getFirstname() != null) {
            model.setFirstname(userDTO.getFirstname());
        }
        if (userDTO.getLastname() != null) {
            model.setLastname(userDTO.getLastname());
        }
        if (userDTO.getEmail() != null) {
            model.setEmail(userDTO.getEmail());
        }
        if (userDTO.getRole() != null) {
            model.setRole(userDTO.getRole());
        }
        if (userDTO.getPassword() != null) {
            model.setPassword(userDTO.getPassword());
        }
        model.setUsername(obj.getUsername());
        model.setKeycloak_id(obj.getId());
        userService.save(model);

        String client_id = keycloakService.getClientId(keycloak);

        List<RoleRepresentation> roleToAdd = new LinkedList<>();
        roleToAdd.add(keycloak
                .realm(configurationService.getRealm())
                .clients()
                .get(client_id)
                .roles()
                .get(userDTO.getRole())
                .toRepresentation()
        );
        userResource1.roles().
                clientLevel(client_id).
                add(roleToAdd);
        usersResource.get(userId).update(user);

        return "User Details Updated Successfully";
    }

    @PutMapping(path = "/remove_role/{userId}")
    public String removeRole(@PathVariable("userId") String userId) {
        Keycloak keycloak = keycloakService.getToken();
        RealmResource realmResource = keycloak.realm(configurationService.getRealm());
        UsersResource usersResource = realmResource.users();

        UserRepresentation user = new UserRepresentation();

        UserResource userResource1 = keycloak
                .realm(configurationService.getRealm())
                .users()
                .get(userId);

        String client_id = keycloakService.getClientId(keycloak);

        List<RoleRepresentation> roleToAdd = new LinkedList<>();
        roleToAdd.add(keycloak
                .realm(configurationService.getRealm())
                .clients()
                .get(client_id)
                .roles()
                .get(userResource1.roles().clientLevel(client_id).listAll().get(0).getName())
                .toRepresentation());
        userResource1.roles().clientLevel(client_id).remove(roleToAdd);
        System.out.println(roleToAdd);
        usersResource.get(userId).update(user);
        System.out.println(userResource1.roles().clientLevel(client_id).listAll());
        UserRepresentation obj = usersResource.get(userId).toRepresentation();
        final UserDTO model = userService.findByUsername(obj.getUsername());
        System.out.println(model);
        model.setRole(null);
        userService.save(model);
        return "User Role Removed Successfully";
    }

    @GetMapping("/userinfo")
    public UserDTO userInfoController(UserDTO userDTO, Principal principal) {
        KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;
        AccessToken accessToken = keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken();
        String login = accessToken.getPreferredUsername();
        UserDTO model = userService.findByUsername(login);
        userDTO.setUsername(accessToken.getPreferredUsername());
        userDTO.setFirstname(accessToken.getGivenName());
        userDTO.setLastname(accessToken.getFamilyName());
        userDTO.setEmail(accessToken.getEmail());
        if (model != null) {
            userDTO.setId(model.getId());
            userDTO.setKeycloak_id(model.getKeycloak_id());
            userDTO.setCompany_id(model.getCompany_id());
            userDTO.setPassword(model.getPassword());
            userDTO.setRole(model.getRole());
        }
        return userDTO;
    }


    @PostMapping(path = "/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {

        UserDTO userDTO1 = new UserDTO();
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());
        Keycloak keycloak = keycloakService.getToken();

        RealmResource realmResource = keycloak.realm(configurationService.getRealm());
        UsersResource usersResource = realmResource.users();

        Response response = usersResource.create(user);

        userDTO1.setFirstname(user.getUsername());
        userDTO1.setLastname(user.getLastName());
        userDTO1.setEmail(user.getEmail());
        userDTO1.setUsername(user.getUsername());
        userDTO1.setPassword(userDTO.getPassword());
        userDTO1.setRole(userDTO.getRole());
        if (appController.findById(userDTO.getCompany_id())) {
                userDTO1.setCompany_id(userDTO.getCompany_id());
            }
        else {
            return new ResponseEntity<Object>(
                    "Такой компании не существует!", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (response.getStatus() == 201) {

            String userId = CreatedResponseUtil.getCreatedId(response);

            log.info("Created userId {}", userId);
            System.out.println("Created userId {}" + userId);

            userDTO1.setKeycloak_id(userId);


            keycloakService.setPassword(userDTO, usersResource, userId);

            String client_id = keycloakService.getClientId(keycloak);
            UserResource userResource1 = keycloak
                    .realm(configurationService.getRealm())
                    .users()
                    .get(userId);
            List<RoleRepresentation> roleToAdd = new LinkedList<>();
            roleToAdd.add(keycloak
                    .realm(configurationService.getRealm())
                    .clients()
                    .get(client_id)
                    .roles()
                    .get(userDTO.getRole())
                    .toRepresentation()
            );
            userResource1.roles().clientLevel(client_id).add(roleToAdd);
        }
        if (userDTO1.getKeycloak_id() != null) {
            userService.save(userDTO1);
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

    @GetMapping(path = "/{userId}")
    public Boolean findById(@PathVariable("userId") long userId) {
        return userService.findByUserId(userId);
    }

    @GetMapping(path = "getOne/{userId}")
    public String getOne(@PathVariable("userId") long userId) throws JsonProcessingException {
        UserDTO user = userService.getByUserId(userId);
        UserDTO u = new UserDTO();
        u.setId(user.getId());
        u.setPassword(user.getPassword());
        u.setRole(user.getRole());
        u.setLastname(user.getLastname());
        u.setUsername(user.getUsername());
        u.setEmail(user.getEmail());
        u.setFirstname(user.getFirstname());
        u.setKeycloak_id(user.getKeycloak_id());
        u.setCompany_id(user.getCompany_id());

        String json = new ObjectMapper().writeValueAsString(u);

        Object userPrettyJson = new ObjectMapper().readValue(
                json, UserDTO.class);

        String obj = new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(userPrettyJson);

        return obj;
    }

    @GetMapping(path = "/all")
    public List<String> getAll() {
        List<String> userDTOs = new ArrayList<>();
        List<UserDTO> users = userService.getAll();
        for (UserDTO user : users) {
            UserDTOCompany userDTOCompany = new UserDTOCompany();
            userDTOCompany.setId(user.getId());
            userDTOCompany.setUsername(user.getUsername());
            userDTOCompany.setFirstname(user.getFirstname());
            userDTOCompany.setLastname(user.getLastname());
            userDTOCompany.setEmail(user.getEmail());
            userDTOCompany.setKeycloak_id(user.getKeycloak_id());
            userDTOCompany.setRole(user.getRole());
            if (user.getCompany_id() != null) {
                String company = appController.getOne(user.getCompany_id());
                JSONObject jsonObject = new JSONObject(company);
                userDTOCompany.setCompany_id(String.valueOf(jsonObject.get("companyId")));
                userDTOCompany.setName(String.valueOf(jsonObject.get("name")));
                userDTOCompany.setShortName(String.valueOf(jsonObject.get("shortName")));
                userDTOCompany.setOgrn(String.valueOf(jsonObject.get("ogrn")));

                if (!Objects.equals(String.valueOf(jsonObject.get("address")), "null")) {
                    userDTOCompany.setAddressId(String.valueOf(jsonObject.getJSONObject("address").get("addressId")));
                    userDTOCompany.setStreet(String.valueOf(jsonObject.getJSONObject("address").get("street")));
                    userDTOCompany.setArea(String.valueOf(jsonObject.getJSONObject("address").get("area")));
                    userDTOCompany.setCity(String.valueOf(jsonObject.getJSONObject("address").get("city")));
                    userDTOCompany.setIndex(String.valueOf(jsonObject.getJSONObject("address").get("index")));
                    userDTOCompany.setHomeNumber(String.valueOf(jsonObject.getJSONObject("address").get("homeNumber")));
                    userDTOCompany.setOfficeNumber(String.valueOf(jsonObject.getJSONObject("address").get("officeNumber")));
                }
            }
            userDTOs.add(userDTOCompany.toString());
        }
        return userDTOs;
    }
}
