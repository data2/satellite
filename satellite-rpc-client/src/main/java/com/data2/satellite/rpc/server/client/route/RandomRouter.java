package com.data2.satellite.rpc.server.client.route;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * @author data2
 * @description
 * @date 2021/4/2 下午3:46
 */
@Component
public class RandomRouter implements Router {
    private static Random random = new Random();

    @Override
    public String route(List<String> sources) {
        return sources.get(random.nextInt(sources.size()));
    }
}
