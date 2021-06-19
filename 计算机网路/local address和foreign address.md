# 摘抄1

Going over what netstat is prior to jumping into the difference between local and foreign address seems good idea to me. Netstat is basically a network utility tool that displays network connections (TCP, UDP), routing tables, number of network interface and network protocol statistics.

The command is often used to find problems in the network, or determine the amount of traffic over the network as a performance measurement. The command is available in different OS platforms, like Windows, Linux, Solaris, BSD. There are many parameters to this command. Try this [link ](https://en.wikipedia.org/wiki/Netstat)to find more about them.

Now over to the difference in local and foreign address. In windows local address is displayed on the second column. It is the IP address of the local computer (your device) and the port number being used. This address is assigned to you by your router DHCP servers. DHCP (Dynamic Host Configuration Protocol) is a network protocol that automatically assigns IP address to your computer from a predefined range of number configured for a given network. Foreign address is displayed on the third column. It is the IP address and port number of the remote computer to which the socket is connected. Simply speaking local address is the IP address of your device, while foreign address is the address of the device you are connected to it.

The difference between 127.0.0.1 and your local IP address can be understood by pinging. 127.0.0.1 is a loop back address. This is the address if pinged will ping your own network card. If you ping the IP address (local address) provided to you by DHCP, you are doing the same thing again, but going through your router and back.

# 摘抄2

The local address is of interest because a host may have more than one address (for multiple interfaces), and the local socket may bind to a specific interface; although most often it will bind to “any” interface (represented by * or 0.0.0.0/0) and let IP routing take care of which actual interface to use for the traffic. Similarly a socket can be bound to a specified port (handy for servers), but for a client process the stack will usually assign a free port number from a range of “working” ports; and netstat can then be used to find the port number being used by a connection of interest.

The foreign address column in netstat is the other end of the connection, which I called the “remote” end. But it could actually be on the same host, say a server process accessed by 127.0.0.1:<port>, in which case it is not *strictly* remote. To be nerdishly correct about it netstat labels the column Foreign Address. I think “target” or “destination” would be more meaningful, but I suppose that might imply the direction of flow; so “Foreign” it is.