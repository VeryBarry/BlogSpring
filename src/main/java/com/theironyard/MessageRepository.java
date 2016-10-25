package com.theironyard;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by VeryBarry on 10/25/16.
 */
public interface MessageRepository extends CrudRepository<Message, Integer> {
}
