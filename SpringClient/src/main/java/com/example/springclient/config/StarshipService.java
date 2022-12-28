package com.example.springclient.config;


/**
 * Author: Vyacheslav Chernyshov
 * Date: 12.02.19
 * Time: 22:23
 * e-mail: 2262288@gmail.com
 */
public interface StarshipService {

    StarshipDto save(StarshipDto dto);

    void send(StarshipDto dto);

    void consume(StarshipDto dto);
}