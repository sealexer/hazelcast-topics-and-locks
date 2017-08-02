package com.alex.test.hazelcast.config;

import com.alex.test.hazelcast.topic.TopicListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alexander Shusherov
 */
//@Configuration
public class ApplicationConfiguration {

  @Bean
  public TopicListener topicListener() {
    return new TopicListener();
  }

}
