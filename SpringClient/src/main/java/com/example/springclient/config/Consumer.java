package com.example.springclient.config;

import com.example.springclient.AppController;
import com.example.springclient.models.UserDTO;
import com.example.springclient.service.UserService;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    private UserService userService;

    private AppController appController;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = "delete_companies", groupId = "group_id")
    public void consume(String message) throws IOException {
        System.out.println(message);
        logger.info(String.format("#### -> Producing message -> %s", message));
        List<UserDTO> users = userService.findByCompanyId(Integer.valueOf(message));
        for (UserDTO userdto : users) {
            userService.updateCompany(Math.toIntExact(userdto.getId()));
        }
    }
}