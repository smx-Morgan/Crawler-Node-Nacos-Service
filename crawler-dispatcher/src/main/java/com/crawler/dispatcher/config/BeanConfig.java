package com.crawler.dispatcher.config;

import com.crawler.api.entity.TaskContainer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Configuration
public class BeanConfig {
    @Bean
    public IRule customRule() {
        return new RoundPortSmallerRule();
    }

    @Bean
    public TaskContainer taskContainer() {
        return new TaskContainer();
    }

    @Slf4j
    public static class RoundPortSmallerRule extends AbstractLoadBalancerRule {
        private final AtomicInteger serverCyclicCounter = new AtomicInteger(-1);

        @Override
        public Server choose(Object key) {
            return choose(getLoadBalancer(), key);
        }

        public Server choose(ILoadBalancer lb, Object key) {
            if (lb == null) {
                log.warn("no load balancer");
                return null;
            }

            Server server;
            int count = 0;

            // 选择过程重试10次，超过10次，认为失败返回null
            while (count++ < 10) {
                // 获取服务列表，按端口排序，从小到大
                List<Server> sortedServerList = lb.getReachableServers()
                        .stream()
                        .sorted(Comparator.comparingInt(Server::getPort))
                        .collect(Collectors.toList());

                if (sortedServerList.isEmpty()) {
                    // No servers
                    log.warn("No up servers available from load balancer: " + lb);
                    return null;
                }

                /*
                 * 核心代码，通过轮询计数器递增并获取新下标，
                 * 通过下标选择Server
                 */
                int nextServerIndex = incrAndGetRoundIndex(sortedServerList.size());
                server = sortedServerList.get(nextServerIndex);

                // 服务临时不可用
                if (server == null) {
                    // 让出cpu时间片，稍后重试
                    Thread.yield();
                    continue;
                }

                if (server.isAlive() && (server.isReadyToServe())) {
                    log.info("调度至：{}", server.getId());
                    return (server);
                }
            }

            if (count >= 10) {
                log.warn("No available alive servers after 10 tries from load balancer: " + lb);
            }

            return null;
        }

        @SuppressWarnings("SpellCheckingInspection")
        private int incrAndGetRoundIndex(int sizeLimit) {
            for (; ; ) {
                int current = serverCyclicCounter.get();
                int next = (current + 1) % sizeLimit;
                if (serverCyclicCounter.compareAndSet(current, next))
                    return next;
            }
        }

        @Override
        public void initWithNiwsConfig(IClientConfig clientConfig) {/* do nothing... */}

    }

}
