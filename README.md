# Satellite
自研可控的简易远程调用RPC框架，采用netty+zookeeper实现。

    1、支持命名空间隔离服务，减少互相影响
    2、服务启动自动注册
    3、客户端可选择多种路由策略
    4、注册中心当机，客户端仍然可以使用缓存
    5、自动监听服务的状态变化，实时通知客户端
    

使用方式

    服务端
    
    a、添加pom依赖
        <dependency>
            <groupId>com.data2</groupId>
            <artifactId>satellite-rpc-server</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.data2</groupId>
            <artifactId>example-server-api</artifactId>
            <version>1.0.0</version>
        </dependency>           

    b、添加配置
        easy:
          rpc:
            registry:
              address: localhost:2181
              namespace: rpc_namespace
            server:
              address: localhost:8081
        server:
          port: 8081

    客户端
    
    a、添加pom依赖
        <dependency>
            <groupId>com.data2</groupId>
             <artifactId>satellite-rpc-client</artifactId>
             <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.data2</groupId>
            <artifactId>example-server-api</artifactId>
            <version>1.0.0</version>
        </dependency>
        
    b、添加配置
        easy:
          rpc:
            registry:
              address: localhost:2181
              namespace: rpc_namespace
            client:
              routeName: randomRouter
        
        server:
          port: 8082