# # 一、interest operation set和native poll event set的对应关系

~~~
SelectionKey.OP_ACCEPT -> Net.POLLIN

POLLIN There is data to read.

/**
* Translates an interest operation set into a native poll event set
*/
public int translateInterestOps(int ops) {
  int newOps = 0;
  if ((ops & SelectionKey.OP_ACCEPT) != 0)
    	newOps |= Net.POLLIN;
  return newOps;
}


public int translateInterestOps(int ops) {
  int newOps = 0;
  if ((ops & SelectionKey.OP_READ) != 0)
  	newOps |= Net.POLLIN;
  if ((ops & SelectionKey.OP_WRITE) != 0)
  	newOps |= Net.POLLOUT;
  if ((ops & SelectionKey.OP_CONNECT) != 0)
  	newOps |= Net.POLLCONN;
  return newOps;
}
~~~



