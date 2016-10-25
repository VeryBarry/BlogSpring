package com.theironyard;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by VeryBarry on 10/25/16.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String name);
}