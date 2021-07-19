# Net.java

~~~java
public static final short POLLIN;
public static final short POLLOUT;
public static final short POLLERR;
public static final short POLLHUP;
public static final short POLLNVAL;
public static final short POLLCONN;
~~~

# Poll.h

~~~c
/*
 * This file is intended to be compatible with the traditional poll.h.
 */

/*
 * Requestable events.  If poll(2) finds any of these set, they are
 * copied to revents on return.
 */
#define POLLIN          0x0001          /* any readable data available */
#define POLLPRI         0x0002          /* OOB/Urgent readable data */
#define POLLOUT         0x0004          /* file descriptor is writeable */
#define POLLRDNORM      0x0040          /* non-OOB/URG data available */
#define POLLWRNORM      POLLOUT         /* no write type differentiation */
#define POLLRDBAND      0x0080          /* OOB/Urgent readable data */
#define POLLWRBAND      0x0100          /* OOB/Urgent data can be written */

/*
 * FreeBSD extensions: polling on a regular file might return one
 * of these events (currently only supported on local filesystems).
 */
#define POLLEXTEND      0x0200          /* file may have been extended */
#define POLLATTRIB      0x0400          /* file attributes may have changed */
#define POLLNLINK       0x0800          /* (un)link/rename may have happened */
#define POLLWRITE       0x1000          /* file's contents may have changed */

/*
 * These events are set if they occur regardless of whether they were
 * requested.
 */
#define POLLERR         0x0008          /* some poll error occurred */
#define POLLHUP         0x0010          /* file descriptor was "hung up" */
#define POLLNVAL        0x0020          /* requested events "invalid" */

#define POLLSTANDARD    (POLLIN|POLLPRI|POLLOUT|POLLRDNORM|POLLRDBAND|\
	                 POLLWRBAND|POLLERR|POLLHUP|POLLNVAL)
~~~



