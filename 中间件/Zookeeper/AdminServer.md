https://zookeeper.apache.org/doc/r3.6.2/zookeeperAdmin.html#sc_adminserver_config

New in 3.6.0: The following options are used to configure the AdminServer.

    admin.portUnification : (Java system property: zookeeper.admin.portUnification) Enable the admin port to accept both HTTP and HTTPS traffic. Defaults to disabled.

New in 3.5.0: The following options are used to configure the AdminServer.

    admin.enableServer : (Java system property: zookeeper.admin.enableServer) Set to "false" to disable the AdminServer. By default the AdminServer is enabled.
    
    admin.serverAddress : (Java system property: zookeeper.admin.serverAddress) The address the embedded Jetty server listens on. Defaults to 0.0.0.0.
    
    admin.serverPort : (Java system property: zookeeper.admin.serverPort) The port the embedded Jetty server listens on. Defaults to 8080.
    
    admin.idleTimeout : (Java system property: zookeeper.admin.idleTimeout) Set the maximum idle time in milliseconds that a connection can wait before sending or receiving data. Defaults to 30000 ms.
    
    admin.commandURL : (Java system property: zookeeper.admin.commandURL) The URL for listing and issuing commands relative to the root URL. Defaults to "/commands".