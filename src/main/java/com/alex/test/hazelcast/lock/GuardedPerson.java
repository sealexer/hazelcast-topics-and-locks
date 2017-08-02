package com.alex.test.hazelcast.lock;

import com.alex.test.hazelcast.model.Person;
import java.io.Serializable;
import lombok.Data;

/**
 * @author Alexander Shusherov
 */
@Data
public class GuardedPerson implements Serializable {

  private final Person person;
  private final String lockName;

}
