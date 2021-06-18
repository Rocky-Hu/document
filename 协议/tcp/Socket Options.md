https://man7.org/linux/man-pages/man7/socket.7.htmls

# SO_REUSEADDR

SO_REUSEADDR allows your server to bind to an address which is in a TIME_WAIT state.

> This socket option tells the kernel that even if this port is busy (in the TIME_WAIT state), go ahead and reuse it anyway.  If it is busy,  but with another state, you will still get an address already in use error.  It is useful if your server has been shut down, and then restarted right away while sockets are still active on its port.  You should be aware that if any unexpected data comes in, it may confuse your server, but while this is possible, it is not likely.
>
> It has been pointed out that "A socket is a 5 tuple (proto, local addr, local port, remote addr, remote port).  SO_REUSEADDR just says that you can reuse local addresses.  The 5 tuple still must be unique!" by Michael Hunter (mphunter@qnx.com).  This is true, and this= is why it is very unlikely that unexpected data will ever be seen by your server.  The danger is that such a 5 tuple is still floating around on the net, and while it is bouncing around, a new connection from the same client, on the same system, happens to get the same remote port.  This is explained by Richard Stevens in ``2.7 Please explain the TIME_WAIT state.''.

