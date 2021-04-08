package com.data2.satellite.rpc.server.server;

import com.data2.satellite.rpc.common.configuration.ServerConfig;
import com.data2.satellite.rpc.common.protocol.RpcDecoder;
import com.data2.satellite.rpc.common.protocol.RpcEncoder;
import com.data2.satellite.rpc.common.protocol.RpcRequest;
import com.data2.satellite.rpc.common.protocol.RpcResponse;
import com.data2.satellite.rpc.server.registry.ServiceRegistry;
import com.data2.satellite.rpc.server.server.anno.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Data
@Component
public class RpcServer implements ApplicationContextAware, CommandLineRunner, DisposableBean {

    private static ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private ServiceRegistry serviceRegistry;
    private Map<String, Object> handlerMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    @Override
    public void run(String... args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new RpcHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] array = serverConfig.getAddress().split(":");
            ChannelFuture future = bootstrap.bind(array[0], Integer.parseInt(array[1])).sync();
            log.debug("Server started on port {}", Integer.parseInt(array[1]));

            serviceRegistry.registerServerAndService(serverConfig.getAddress(), handlerMap);
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    @Override
    public void destroy() throws Exception {
        log.info("1233");
    }
}
