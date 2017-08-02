package com.alex.test.hazelcast.controllers;

import com.alex.test.hazelcast.lock.GuardedPerson;
import com.alex.test.hazelcast.model.Person;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ReplicatedMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * @author Alexander Shusherov
 */
@RestController
public class TopicController {


  private static final Logger log = LoggerFactory.getLogger(TopicController.class);
  private static final int NUMBER_OF_ITERATIONS = 1;

  private final HazelcastInstance hazelcastInstance;
  private final AtomicInteger counter = new AtomicInteger();

  @Autowired
  public TopicController(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  @RequestMapping(value = "send", method = RequestMethod.GET)
  public String sendToTopic() {

    for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
      sendOneObject(counter.getAndIncrement());
    }

    return "OK";
  }

  private void sendOneObject(int i) {
    GuardedPerson guardedPerson = createGuardedPerson(i);
    Person person = guardedPerson.getPerson();
    String lockName = guardedPerson.getLockName();
//    String lockName = String.valueOf(person.getId());

    ReplicatedMap<String, Person> lockToPersonMap =
        hazelcastInstance.getReplicatedMap("persons");
    log.debug("Placing object to map: {}", person);
    lockToPersonMap.put(lockName, person);

    ITopic<String> topic = hazelcastInstance.getTopic("topic");
    log.debug("Publishing object's lock name: {}", lockName);
    topic.publish(lockName);
  }

  private GuardedPerson createGuardedPerson(int i) {
    String randomUUID = UUID.randomUUID().toString();
    Person person = new Person("Name " + randomUUID, i);
    return new GuardedPerson(person, randomUUID);
  }

}
