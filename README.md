# hazelcast-topics-and-locks

#### Test-driving Hazelcast topics and distributed locks.

```
This is a simple prototype to check if hazelcast can be used 
for building a cluster of processing nodes supporting failover.  
```

* Run 2 instances of `HazelTopicsAndLocksApplication` (say, on ports 8080 and 8081)
* Invoke [http://localhost:8080/send](http://localhost:8080/send)
* Observe nodes competing for locks (in logs)
* Kill one of nodes
* Observe the other one to proceed processing the incoming "queue"
