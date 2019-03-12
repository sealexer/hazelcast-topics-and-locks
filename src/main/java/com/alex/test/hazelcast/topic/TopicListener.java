package com.alex.test.hazelcast.topic;

import com.alex.test.hazelcast.model.Person;
import com.alex.test.hazelcast.util.ThreadUtil;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.core.ReplicatedMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TopicListener implements MessageListener<String> {

  private static final Logger log = LoggerFactory.getLogger(TopicListener.class);

  private HazelcastInstance hazelcastInstance;
  private final AtomicInteger counter = new AtomicInteger();

//  @Autowired
  public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  @Override
  public void onMessage(Message<String> message) {

    String lockName = message.getMessageObject();

    if (!isInitialized()) {
      log.warn("Topic listener has not yet been initialized completely."
          + " Dropping the message. [lockName={}]", lockName);
      return;
    }

    log.debug("Acquiring lock {}", lockName);
    ILock lock = hazelcastInstance.getLock(lockName);

    lock.lock();
    try {
      log.debug("Lock is mine! [lockName={}]", lockName);
      ReplicatedMap<String, Person> lockNameToPersonMap =
          hazelcastInstance.getReplicatedMap("persons");
      Person person = lockNameToPersonMap.get(lockName);
      if (person != null) {
        log.debug("Person presents in map [person={}]", person);

        ThreadUtil.someHeavyOperation();

        log.debug("Removing person from map [person={}]", person);
        lockNameToPersonMap.remove(lockName);
      } else {
        log.debug("Person is NOT in map any longer. Doing nothing [lockName={}]", lockName);
      }
    } finally {
      log.debug("Releasing the lock {}", lockName);
      lock.unlock();
    }

    log.debug("By now listener has been invoked {} times", counter.incrementAndGet());
  }

  private boolean isInitialized() {
    return hazelcastInstance != null;
  }

//  @PostConstruct
//  private void addToHzConfig() {
//    hazelcastInstance
//        .getConfig()
//        .getTopicConfig("topic")
//        .addMessageListenerConfig(
//            new ListenerConfig(this)
//        );
//  }

}
