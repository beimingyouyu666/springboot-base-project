# springboot staging springboot项目脚手架


## 引入springcloud的版本依赖
```java
    <!--通过这个方式引入springcloud的版本-->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

## 整合mybatis

1、引入依赖

```java
        <!--整合mybatis-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.0.0</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.28</version>
        </dependency>

        <!--使用druid作为数据库连接池-->
        <!--<dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>-->
        <!--整合mybatis-->
```
2、加上配置

```java
#数据库配置
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://114.215.147.110:3306/yangk?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=Yk.666666

#使用druid作为数据库连接池则加上此配置
#spring.datasource.type=com.alibaba.druid.pool.DruidDataSource

#mybatis指定mapper路径
mybatis.mapper-locations=classpath:mapper/*.xml
```

3、启动类上加上注解 @MapperScan("com.yangk.baseproject.mapper")  ，指定mapper接口存放的文件夹

## 整合mybatis-generator（mybatis插件）
1、引入依赖
```java
<!--引入mybatis-generator（mybatis插件）-->
        <dependency>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-core</artifactId>
            <version>${mybatis-generator.version}</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-maven-plugin</artifactId>
            <version>${mybatis-generator.version}</version>
        </dependency>
        <!--引入mybatis-generator（mybatis插件）-->
```

2、引入插件

```java
<!--引入mybatis-generator（mybatis插件）-->
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>${mybatis-generator.version}</version>
                <configuration>
                    <verbose>true</verbose>
                    <overwrite>true</overwrite>
                </configuration>
            </plugin>
            <!--引入mybatis-generator（mybatis插件）-->
```

3、加上配置，下载 mysql-connector-java-5.1.48.jar，在配置文件中指定目录
```java
#mybatis-generator配置
spring.datasource.driver.location=E:/svncodes/springboot-base-project/src/main/resources/mysql/mysql-connector-java-5.1.48.jar
```

4、generatorConfig.xml 配置文件

```java
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>

    <!--导入属性配置-->
    <properties resource="application.properties"></properties>

    <!--指定特定数据库的jdbc驱动jar包的位置-->
    <classPathEntry location="${spring.datasource.driver.location}"/>

    <context id="default" targetRuntime="MyBatis3">

        <!-- optional，旨在创建class时，对注释进行控制 -->
        <commentGenerator>
            <property name="suppressDate" value="true"/>
            <property name="suppressAllComments" value="true"/>
        </commentGenerator>

        <!--jdbc的数据库连接 -->
        <jdbcConnection
                driverClass="${spring.datasource.driverClassName}"
                connectionURL="${spring.datasource.url}"
                userId="${spring.datasource.username}"
                password="${spring.datasource.password}">
        </jdbcConnection>


        <!-- 非必需，类型处理器，在数据库类型和java类型之间的转换控制-->
        <javaTypeResolver>
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>


        <!-- Model模型生成器,用来生成含有主键key的类，记录类 以及查询Example类
            targetPackage     指定生成的model生成所在的包名
            targetProject     指定在该项目下所在的路径
        -->
        <!--<javaModelGenerator targetPackage="com.yangk.baseproject.domain.dos" targetProject=".\src\main\java">-->
        <javaModelGenerator targetPackage="com.yangk.baseproject.domain.dos" targetProject="./src/main/java">
            <!-- 是否允许子包，即targetPackage.schemaName.tableName -->
            <property name="enableSubPackages" value="false"/>
            <!-- 是否对model添加 构造函数 -->
            <property name="constructorBased" value="false"/>
            <!-- 是否对类CHAR类型的列的数据进行trim操作 -->
            <property name="trimStrings" value="true"/>
            <!-- 建立的Model对象是否 不可改变  即生成的Model对象不会有 setter方法，只有构造方法 -->
            <property name="immutable" value="false"/>
        </javaModelGenerator>

        <!--mapper映射文件生成所在的目录 为每一个数据库的表生成对应的SqlMap文件 -->
        <!--<sqlMapGenerator targetPackage="mapper" targetProject=".\src\main\resources">-->
        <sqlMapGenerator targetPackage="mapper" targetProject="./src/main/resources">
            <property name="enableSubPackages" value="false"/>
        </sqlMapGenerator>

        <!-- 客户端代码，生成易于使用的针对Model对象和XML配置文件 的代码
                type="ANNOTATEDMAPPER",生成Java Model 和基于注解的Mapper对象
                type="MIXEDMAPPER",生成基于注解的Java Model 和相应的Mapper对象
                type="XMLMAPPER",生成SQLMap XML文件和独立的Mapper接口
        -->

        <!-- targetPackage：mapper接口dao生成的位置 -->
        <!--<javaClientGenerator type="XMLMAPPER" targetPackage="com.mmall.dao" targetProject=".\src\main\java">-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.yangk.baseproject.mapper" targetProject="./src/main/java">
            <!-- enableSubPackages:是否让schema作为包的后缀 -->
            <property name="enableSubPackages" value="false" />
        </javaClientGenerator>


        <table tableName="person" domainObjectName="Person" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false" ></table>

    </context>
</generatorConfiguration>
```

5、加一个项目启动配置 maven -》 command line 配置为 “mybatis-generator:generate”

6、启动后就会自动生成数据库表对应的实体，mapper接口文件，mapper.xml的sql文件

## 使用consul作为注册中心和配置中心

```java
        <!--注册到consul上-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>
        
        <!--健康检查-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!--使用consul做配置中心，读取consul上的配置-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-config</artifactId>
        </dependency>
```

## pom文件中根据profile属性，动态指定consul注册中心地址

pom.xml配置

```java
    <profiles>
        <profile>
            <id>local</id>
            <properties>
                <profiles.active>local</profiles.active>
                <consul.host>http://consul.dev.i4px.com</consul.host>
                <consul.port>8500</consul.port>
                <config.prefix>gds</config.prefix>
            </properties>
        </profile>

        <profile>
            <id>uat</id>
            <properties>
                <profiles.active>uat</profiles.active>
                <consul.host>http://consul.uat.i4px.com</consul.host>
                <consul.port>8500</consul.port>
                <config.prefix>gds</config.prefix>
            </properties>
        </profile>

        <profile>
            <id>US</id>
            <properties>
                <profiles.active>US</profiles.active>
                <consul.host>http://10.105.4.99</consul.host>
                <consul.port>8500</consul.port>
                <config.prefix>gds</config.prefix>
            </properties>
        </profile>
    </profiles>
    
    <!--必须加上这个配置，不然bootstrap.properties文件读取不到这里profile定义的变量-->
    <build>
            <resources>
                <resource>
                    <directory>src/main/resources</directory>
                    <filtering>true</filtering>
                </resource>
            </resources>
        </build>
    
    
```

bootstrap.properties 中配置consul的地址
```java
spring.profiles.active=@profiles.active@
spring.cloud.consul.host=@consul.host@
spring.cloud.consul.config.prefix=@config.prefix@
spring.cloud.consul.port=@consul.port@
```

## 整合rabbitmq

1、引入依赖

```java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
```
2、全局的mq配置文件 spring-rabbitmq.xml

定义 connectionFactory、rabbitAdmin、rabbitTemplate、队列配置文件路径、正常交换器、死信交换器

3、默认的 SimpleMessageConverter 在发送消息时会将对象序列化成字节数组，若要反序列化对象，需要自定义 MessageConverter

RabbitMQ 提供了 Jackson2JsonMessageConverter 来支持消息内容 JSON 序列化与反序列化.消息发送者在发送消息时应设置 MessageConverter 为 Jackson2JsonMessageConverter

自定义消息转换为json的类 com.yangk.baseproject.common.rabbitmq.converter.FastJsonMessageConverter

自定义发送消息成功回复接收类 com.yangk.baseproject.common.rabbitmq.service.impl.RabbitmqConfirmServiceImpl

4、定义消费者基类 com.yangk.baseproject.common.rabbitmq.consumer.AbstractRabbitConsumer

消费成功则响应 basicAck，通知mq删除此条消息

消费失败则响应 basicReject，通知mq不要重发，将其转入死信队列

根据消息头中“x-death”属性判断，如果消息进入死信队列超过5次则通知mq删除消息，此处应保存消息到数据库，发送告警信息

5、实现一条消息发送到两个队列，将两个队列绑定在同一个交换器，路由配置为同一个，并且发送消息时，指定交换器、路由即可

6、死信队列的配置

队列test2配置了“<entry key="x-dead-letter-routing-key" value="springboot-base-project-rabbit-r-test-dead" />”，所以在死信交换器绑定死信队列的时候，对应的路由也要配置，不然就会使用正常队列的路由，导致消费失败的消息未能发送到死信队列

## 使用logback作为日志框架

配置文件名必须是： logback-spring.xml

不然在配置文件中指定： 
logging.config=classpath:logback-spring.xml

可以根据profile设置日志的打印级别

## 整合redis

springboot2默认使用lettuce连接池

1、引入依赖
```java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
```

2、加上配置

```java
spring.redis.cluster.nodes=10.104.6.131:7000,10.104.6.131:7001,10.104.6.131:7002
spring.redis.password=
spring.redis.lettuce.pool.max-active=5000
spring.redis.lettuce.pool.max-idle=100
spring.redis.lettuce.pool.min-idle=5
spring.redis.lettuce.pool.max-wait=1000ms
spring.redis.timeout=5000ms
```

3、全局配置文件

com.yangk.baseproject.common.redis.RedisConfig 

指定序列化方式

## 通过jedis使用redis

1、引入依赖
```java
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>
```

2、进行配置 com.yangk.baseproject.common.config.RedisClusterConfig ，注入bean JedisCluster

使用配置类 RedisClusterProperties ，实时刷新配置

3、jedis工具类 com.yangk.baseproject.common.util.JedisClusterUtil

通过JedisCluster来操作redis

## 通过redisson实现分布式锁

redisson所有指令都通过lua脚本执行，redis支持lua脚本原子性执行

redisson设置一个key的默认过期时间为30s,如果某个客户端持有一个锁超过了30s怎么办？

redisson中有一个watchdog的概念，翻译过来就是看门狗，它会在你获取锁之后，每隔10秒帮你把key的超时时间设为30s，这样的话，就算一直持有锁也不会出现key过期了，其他线程获取到锁的问题了。redisson的“看门狗”逻辑保证了没有死锁发生。
(如果机器宕机了，看门狗也就没了。此时就不会延长key的过期时间，到了30s之后就会自动过期了，其他线程可以获取到锁)

1、引入依赖
```java
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>3.13.1</version>
        </dependency>
```

2、配置 redisson com.yangk.baseproject.common.config.RedissonConfig

主要是设置redis集群节点信息，注入 RedissonClient，用来进行redis操作

3、获取锁代码 com.yangk.baseproject.common.util.RedissonUtil.lock 


## 通过redis实现接口幂等

1、自定义注解 com.yangk.baseproject.common.annotation.IdempotentCache，作用在方法上，有key，value，过期时间的属性

2、定义切面 com.yangk.baseproject.common.aspect.IdempotentCacheAspect 扫描注解 

3、实现逻辑：通过注解指定缓存key，将方法返回值作为缓存value

通过切面获取到方法签名，获取到方法参数，根据注解属性设置的缓存key。方法参数为对象时，要指定uuidName的属性字段，通过反射从对象中获取属性值；方法参数为基本类型，不用指定uuidName，直接取第一个参数值。

加上前缀，组装缓存key值，看redis里面是否有缓存，有缓存则拿到缓存值返回；没有缓存则通过分布式锁，获取锁后执行目标方法，将方法返回值作为缓存value，保存缓存到redis。获取锁失败则抛出异常


## 分布式session整合

添加依赖

```java
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
        </dependency>
```


### 安装nginx测试session

nginx.conf文件修改配置

```java
 upstream upstream_name{
        server 127.0.0.1:8566;
        server 127.0.0.1:8567;
    }
 
 server {
        listen       8080;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            proxy_pass http://upstream_name;
			proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
		
	}
```

访问localhost:8080就会轮询访问8566和8567两个服务器

## 引入Hutool，是一个小而全的Java工具类库，通过静态方法封装，降低相关API的学习成本
         
 https://github.com/looly/hutool
 
 模块	介绍
 hutool-aop	JDK动态代理封装，提供非IOC下的切面支持
 
 hutool-bloomFilter	布隆过滤，提供一些Hash算法的布隆过滤
 
 hutool-cache	简单缓存实现
 
 hutool-core	核心，包括Bean操作、日期、各种Util等
 
 hutool-cron	定时任务模块，提供类Crontab表达式的定时任务
 
 hutool-crypto	加密解密模块，提供对称、非对称和摘要算法封装
 
 hutool-db	JDBC封装后的数据操作，基于ActiveRecord思想
 
 hutool-dfa	基于DFA模型的多关键字查找
 
 hutool-extra	扩展模块，对第三方封装（模板引擎、邮件、Servlet、二维码、Emoji、FTP、分词等）
 
 hutool-http	基于HttpUrlConnection的Http客户端封装
 
 hutool-log	自动识别日志实现的日志门面
 
 hutool-script	脚本执行封装，例如Javascript
 
 hutool-setting	功能更强大的Setting配置文件和Properties封装
 
 hutool-system	系统参数调用封装（JVM信息等）
 
 hutool-json	JSON实现
 
 hutool-captcha	图片验证码实现
 
 hutool-poi	针对POI中Excel和Word的封装
 
 hutool-socket	基于Java的NIO和AIO的Socket封装