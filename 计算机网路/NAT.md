https://www.youtube.com/watch?v=FTUV0t6JaDA

# What Is NAT?

NAT stands for network address translation. It’s a way to map multiple local private addresses to a public one before transferring the information. Organizations that want multiple devices to employ a single IP address use NAT, as do most home routers.

## How Does NAT Work?

Let’s say that there is a laptop connected to a home router. Someone uses the laptop to search for directions to their favorite restaurant. The laptop sends this request in a packet to the router, which passes it along to the web. But first, the router changes the outgoing IP address from a private local address to a public address.

If the packet keeps a private address, the receiving server won’t know where to send the information back to — this is akin to sending physical mail and requesting return service but providing a return address of anonymous. By using NAT, the information will make it back to the laptop using the router’s public address, not the laptop’s private one.

## NAT Types

There are three different types of NATs. People use them for different reasons, but they all still work as a NAT.

### 1. Static NAT

When the local address is converted to a public one, this NAT chooses the same one. This means there will be a consistent public IP address associated with that router or NAT device.

### 2. Dynamic NAT

Instead of choosing the same IP address every time, this NAT goes through a pool of public IP addresses. This results in the router or NAT device getting a different address each time the router translates the local address to a public address.

### 3. PAT

PAT stands for port address translation. It’s a type of dynamic NAT, but it bands several local IP addresses to a singular public one. Organizations that want all their employees’ activity to use a singular IP address use a PAT, often under the supervision of a [network administrator](https://www.comptia.org/blog/your-next-move-network-administrator).

## Why Use NAT?

NAT is a straightforward enough process, but what is the point of it? Ultimately, it comes down to conservation and security.

### IP Conservation

IP addresses identify each device connected to the internet. The existing IP version 4 (IPv4) uses 32-bit numbered IP addresses, which allows for 4 billion possible IP addresses, which seemed like more than enough when it launched in the 1970s.

However, the internet has exploded, and while not all 7 billion people on the planet access the internet regularly, those that do often have multiple connected devices: phones, personal desktop, work laptop, tablet, TV, even refrigerators.

Therefore, the number of devices accessing the internet far surpasses the number of IP addresses available. Routing all of these devices via one connection using NAT helps to consolidate multiple private IP addresses into one public IP address. This helps to keep more public IP addresses available even while private IP addresses proliferate.

On June 6, 2012, IP version 6 (IPv6) officially launched to accommodate the need for more IP addresses. IPv6 uses 128-bit numbered IP addresses, which allow for [exponentially more potential IP addresses](https://www.fcc.gov/consumers/guides/internet-protocol-version-6-ipv6-consumers). It will take many years before this process finishes; so until then, NAT will be a valuable tool.