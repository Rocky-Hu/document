# 一、为什么需要TIME_WAIT状态

> MSL 是Maximum Segment Lifetime英文的缩写，中文可以译为“报文最大生存时间”，他是任何报文在网络上存在的最长时间，超过这个时间报文将被丢弃。
>
> MSL时间是规定，不是算出来的，至少是分钟级别。

TIME_WAIT状态也称为2MSL等待状态。每个具体TCP实现必须选择一个报文段最大生存MSL（Maximum Segment Lifetime）。它是任何报文段被丢弃前在网络内的最长时间。

当TCP执行一个主动关闭，并发回最后一个ACK，该连接必须在TIME_WAIT状态停留的时间为2倍的MSL。这样可让TCP再次发送最后的ACK以防这个ACK丢失（另一端超时并重发最后的FIN）。

这个2MSL等待的另一个结果是这个TCP连接在2MSL等待期间，定义这个连接的插口（客户端IP地址和端口号，服务器的IP地址和端口号）不能再被使用。这个连接只能在2MSL结束后才能再被使用。

## 1.1 阻止延迟数据段

每一个 TCP 数据段都包含唯一的序列号，这个序列号能够保证 TCP 协议的可靠性和顺序性，在不考虑序列号溢出归零的情况下，序列号唯一是 TCP 协议中的重要约定，一旦违反了这条规则，就可能造成令人困惑的现象和结果。为了保证新 TCP 连接的数据段不会与还在网络中传输的历史连接的数据段重复，TCP 连接在分配新的序列号之前需要**至少静默数据段在网络中能够存活的最长时间，即 MSL**。

> To be sure that a TCP does not create a segment that carries a sequence number which may be duplicated by an old segment remaining in the network, the TCP must keep quiet for a maximum segment lifetime (MSL) before assigning any sequence numbers upon starting up or recovering from a crash in which memory of sequence numbers in use was lost.

## 1.2. 保证连接关闭

> TIME-WAIT - represents waiting for enough time to pass to be sure the remote TCP received the acknowledgment of its connection termination request.

如果客户端等待的时间不够长，当服务端还没有收到 `ACK` 消息时，客户端就重新与服务端建立 TCP 连接就会造成以下问题 — 服务端因为没有收到 `ACK` 消息，所以仍然认为当前连接是合法的，客户端重新发送 `SYN` 消息请求握手时会收到服务端的 `RST` 消息，连接建立的过程就会被终止。

# 二、为什么TCP4次挥手时等待为2MSL？

首选我们需要了解如下中要点：

1. TCP连接中的一端发送了FIN报文之后如果收不到对端针对该FIN的ACK，则会反复多次重传FIN报文，大约持续几分钟。
2. 被动关闭处于LAST_ACK状态的一端在收到最后一个ACK之后不会发送任何报文，立即进入CLOSED状态。
3. 主动关闭的一端在收到被动关闭端发送过来的FIN报文并回复ACK之后进入TIME_WAIT状态。
4. 之所以TIME_WAIT状态需要维持一段时间而不是进入CLOSED状态，是因为需要处理对端可能重传的FIN报文或其他一些因网络原因而延迟的数据报文，不处理这些报文可能导致前后两个使用相同四元组的连接中的后一个连接出现异常。
5. 处于TIME_WAIT状态的一端在收到重传的FIN时会重新计时。

下面我们开始分析为什么在发送了最后一个ACK报文之后需要等待2MSL时长来确保没有任何属于当前连接的报文还存活于网络之中（前提是在这2MSL时间内不再收到对方的FIN报文，但即使收到了对端的FIN报文也并不影响我们的讨论，因为如果收到FIN则会回复ACK并重新计时）。

为了便于描述，我们设想有一个处于拆链过程中的TCP连接，这个连接的两端分别是A和B，其中A是主动关闭连接的一端，因为刚刚向对端发送了针对对端发送过来的FIN报文的ACK，此时正处于TIME_WAIT状态；而B是被动关闭的一端，此时正处于LAST_ACK状态，在收到最后一个ACK之前它会一直重传FIN报文直至超时。随着时间的流逝，A发送给B的ACK报文将会有两种结局：

1. ACK报文在网络中丢失；如前所述，这种情况我们不需要考虑，因为除非多次重传失败，否则AB两端的状态不会发生变化直至某一个ACK不再丢失。
2. ACK报文被B接收到。我们假设A发送了ACK报文后过了一段时间t之后B才收到该ACK，则有 0 < t <= MSL。因为A并不知道它发送出去的ACK要多久对方才能收到，所以A至少要维持MSL时长的TIME_WAIT状态才能保证它的ACK从网络中消失。同时处于LAST_ACK状态的B因为收到了ACK，所以它直接就进入了CLOSED状态，而不会向网络发送任何报文。所以晃眼一看，A只需要等待1个MSL就够了，但仔细想一下其实1个MSL是不行的，因为在B收到ACK前的一刹那，B可能因为没收到ACK而重传了一个FIN报文，这个FIN报文要从网络中消失最多还需要一个MSL时长，所以A还需要多等一个MSL。

综上所述，TIME_WAIT至少需要持续2MSL时长，这2个MSL中的第一个MSL是为了等自己发出去的最后一个ACK从网络中消失，而第二MSL是为了等在对端收到ACK之前的一刹那可能重传的FIN报文从网络中消失。另外，虽然说维持TIME_WAIT状态一段时间有2个目的，但这段时间具体应该多长主要是为了达成上述第二个目的而设计的。

# 三、TIME_WAIT和CLOSE_WAIT状态区别

TIME_WAIT 是主动关闭链接时形成的，等待2MSL时间，约4分钟。主要是防止最后一个ACK丢失。 由于TIME_WAIT 的时间会非常长，因此server端应尽量减少主动关闭连接。

CLOSE_WAIT是被动关闭连接是形成的。根据TCP状态机，服务器端收到客户端发送的FIN，则按照TCP实现发送ACK，因此进入CLOSE_WAIT状态。但如果服务器端不执行close()，就不能由CLOSE_WAIT迁移到LAST_ACK，则系统中会存在很多CLOSE_WAIT状态的连接。此时，可能是系统忙于处理读、写操作，而未将已收到FIN的连接，进行close。此时，recv/read已收到FIN的连接socket，会返回0。
