package com.alex.test.hazelcast.config;

import com.alex.test.hazelcast.topic.TopicListener;
import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.MapStoreConfig.InitialLoadMode;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

  private static Logger LOG = LoggerFactory.getLogger(HazelcastConfiguration.class);

  @Bean
  public HazelcastInstance hazelcastInstance() {
    HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig());
    // A hack to resolve cyclic dependency issue
    topicListener().setHazelcastInstance(hazelcastInstance);
    return hazelcastInstance;
  }

  @Bean
  public Config hazelcastConfig() {
    Config config = new Config()
        .setGroupConfig(createGroupConfig())
        .setNetworkConfig(createNetworkConfig())
        .addTopicConfig(createTopicConfig())
        .addReplicatedMapConfig(createMapConfig());
    LOG.debug("Using hazelcast config: {}", config);
    return config;
  }

  private ReplicatedMapConfig createMapConfig() {
    return new ReplicatedMapConfig("persons");
  }

  @Bean
  public TopicListener topicListener() {
    return new TopicListener();
  }


  private GroupConfig createGroupConfig() {
    return new GroupConfig("alsh02141", "alsh02141");
  }

  private NetworkConfig createNetworkConfig() {
    LOG.info("Running hazelcast in LOCAL (non-cloud) mode.");
    return createHazelcastLocalNetworkConfig();
  }

  private NetworkConfig createHazelcastLocalNetworkConfig() {
    return new NetworkConfig()
        .setJoin(new JoinConfig()
            .setMulticastConfig(new MulticastConfig()
                .setEnabled(false))
            .setTcpIpConfig(new TcpIpConfig()
                .setEnabled(true)
                .setMembers(Collections.singletonList("127.0.0.1"))
            ))
        .setInterfaces(new InterfacesConfig()
            .setEnabled(true)
            .addInterface("127.0.0.1"));
  }

  private TopicConfig createTopicConfig() {
    return new TopicConfig("topic")
        .setGlobalOrderingEnabled(true)
        .setStatisticsEnabled(false)
        .addMessageListenerConfig(new ListenerConfig(topicListener()));
  }

}
