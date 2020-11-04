>  参考：https://docs.oracle.com/javase/tutorial/networking/nifs/index.html

网卡或者说网络适配器(`NIC - network interface card`)在Java语言中的抽象。

# 1. [Retrieving Network Interfaces](https://docs.oracle.com/javase/tutorial/networking/nifs/retrieving.html)

~~~java
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import static java.lang.System.out;

public class NetworkInterfaceTest {

    public static void main(String[] args) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface ni : Collections.list(nets)) {
            out.printf("Display name: %s\n", ni.getDisplayName());
            out.printf("Name: %s\n", ni.getName());
            displaySubInterfaces(ni);
            out.printf("\n");
        }
    }

    static void displaySubInterfaces(NetworkInterface netIf) throws SocketException {
        Enumeration<NetworkInterface> subIfs = netIf.getSubInterfaces();

        for (NetworkInterface subIf : Collections.list(subIfs)) {
            out.printf("\tSub Interface Display name: %s\n", subIf.getDisplayName());
            out.printf("\tSub Interface Name: %s\n", subIf.getName());
        }

    }

}
~~~

# 2. [Listing Network Interface Addresses](https://docs.oracle.com/javase/tutorial/networking/nifs/listing.html)

```java
public class ListNets {

    public static void main(String[] args) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            displayInterfaceInformation(netint);
        }
    }

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }
        out.printf("\n");
    }

}
```

# 3. [Network Interface Parameters](https://docs.oracle.com/javase/tutorial/networking/nifs/parameters.html)

~~~java
import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;

public class ListNetsEx {

    public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            displayInterfaceInformation(netint);
        }
    }

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }

        out.printf("Up? %s\n", netint.isUp());
        out.printf("Loopback? %s\n", netint.isLoopback());
        out.printf("PointToPoint? %s\n", netint.isPointToPoint());
        out.printf("Supports multicast? %s\n", netint.supportsMulticast());
        out.printf("Virtual? %s\n", netint.isVirtual());
        out.printf("Hardware address: %s\n",
                Arrays.toString(netint.getHardwareAddress()));
        out.printf("MTU: %s\n", netint.getMTU());
        out.printf("\n");
    }
}
~~~

