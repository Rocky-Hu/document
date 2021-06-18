When you wrote your simple FTP server in project 1, you probably bound your listening socket to the special IP address `INADDR_ANY`. This allowed your program to work without knowing the IP address of the machine it was running on, or, in the case of a machine with multiple network interfaces, it allowed your server to receive packets destined to any of the interfaces. In reality, the semantics of `INADDR_ANY` are more complex and involved.

In the simulator, `INADDR_ANY` has the following semantics: When receiving, a socket bound to this address receives packets from *all* interfaces. For example, suppose that a host has interfaces 0, 1 and 2. If a UDP socket on this host is bound using `INADDR_ANY` and udp port 8000, then the socket will receive all packets for port 8000 that arrive on interfaces 0, 1, or 2. If a second socket attempts to Bind to port 8000 on interface 1, the Bind will fail since the first socket already`owns`that port/interface.

When sending, a socket bound with `INADDR_ANY` binds to the default IP address, which is that of the lowest-numbered interface.

