package com.alex.test.hazelcast.model;

import java.io.Serializable;
import lombok.Data;


@Data
public class Person implements Serializable {

  private final String name;
  private final int id;

}
