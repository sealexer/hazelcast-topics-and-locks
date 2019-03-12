package com.alex.test.hazelcast.controllers;

import com.alex.test.hazelcast.model.Person;
import com.alex.test.hazelcast.util.ThreadUtil;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ReplicatedMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TopicController {

  private static final Logger log = LoggerFactory.getLogger(TopicController.class);
  private static final int NUMBER_OF_ITERATIONS = 100;
  protected static final int SENDING_PERIOD_MILLIS = 500;

  private final HazelcastInstance hazelcastInstance;
  private final AtomicInteger counter = new AtomicInteger();

  @Autowired
  public TopicController(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  @RequestMapping(value = "send", method = RequestMethod.GET)
  public String sendToTopic() {

    new Thread(() -> {
      for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
        sendOneObject(counter.getAndIncrement());
        ThreadUtil.sleep(SENDING_PERIOD_MILLIS);
      }
    }).start();

    return String.format("OK. Will send %1d objects in %2d seconds.",
        NUMBER_OF_ITERATIONS, TimeUnit.MILLISECONDS.toSeconds(NUMBER_OF_ITERATIONS * SENDING_PERIOD_MILLIS));
  }

  private void sendOneObject(int i) {
    String lockName = UUID.randomUUID().toString();
    Person person = createPerson(i);

    ReplicatedMap<String, Person> lockToPersonMap =
        hazelcastInstance.getReplicatedMap("persons");
    log.debug("Placing object to map [key={}, value={}]", lockName, person);
    lockToPersonMap.put(lockName, person);

    ITopic<String> topic = hazelcastInstance.getTopic("topic");
    log.debug("Publishing object's lock name: {}", lockName);
    topic.publish(lockName);
  }

  private Person createPerson(int i) {
    return new Person("Person #" + i, i);
  }

}
