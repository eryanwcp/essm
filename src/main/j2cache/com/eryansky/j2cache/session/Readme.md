 配置web.xml

    <filter>
        <filter-name>j2cache-session-filter</filter-name>
        <filter-class>com.eryansky.j2cache.session.J2CacheSessionFilter</filter-class>
        <init-param><!-- 内存中存放会话数 -->
            <param-name>session.maxSizeInMemory</param-name>
            <param-value>2000</param-value>
        </init-param>
        <init-param><!-- 会话有效期，单位：秒钟 -->
            <param-name>session.maxAge</param-name>
            <param-value>1800</param-value>
        </init-param>
        <!-- cookie configuration -->
        <init-param>
            <param-name>cookie.name</param-name>
            <param-value>J2CACHE_SESSION_ID</param-value>
        </init-param>
        <init-param>
            <param-name>cookie.path</param-name>
            <param-value>/</param-value>
        </init-param>
        <init-param>
            <param-name>cookie.domain</param-name>
            <param-value></param-value>
        </init-param>
        <!-- redis configuration -->
        <init-param>
            <param-name>redis.enabled</param-name>
            <param-value>true</param-value>
        </init-param>
        <init-param>
            <param-name>redis.mode</param-name>
            <param-value>single</param-value>
        </init-param>
        <init-param>
            <param-name>redis.hosts</param-name>
            <param-value>127.0.0.1:6379</param-value>
        </init-param>
        <init-param>
            <param-name>redis.channel</param-name>
            <param-value>j2cache</param-value>
        </init-param>
        <init-param>
            <param-name>redis.cluster_name</param-name>
            <param-value>j2cache</param-value>
        </init-param>
        <init-param>
            <param-name>redis.timeout</param-name>
            <param-value>2000</param-value>
        </init-param>
        <init-param>
            <param-name>redis.password</param-name>
            <param-value></param-value>
        </init-param>
        <init-param>
            <param-name>redis.database</param-name>
            <param-value>0</param-value>
        </init-param>
        <init-param>
            <param-name>redis.maxTotal</param-name>
            <param-value>100</param-value>
        </init-param>
        <init-param>
            <param-name>redis.maxIdle</param-name>
            <param-value>10</param-value>
        </init-param>
        <init-param>
            <param-name>redis.minIdle</param-name>
            <param-value>1</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>j2cache-session-filter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

