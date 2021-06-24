1. `File Descriptors` (FD) are non-negative integers `(0, 1, 2, ...)` that are associated with files that are opened.

2. `0, 1, 2` are standard **FD**'s that corresponds to `STDIN_FILENO`, `STDOUT_FILENO` and `STDERR_FILENO` (defined in `unistd.h`) opened by default on behalf of shell when the program starts.

3. FD's are allocated in the sequential order, meaning the lowest possible unallocated integer value.

   FD按顺序分配，即未分配的最小整数值。

4. FD's for a particular process can be seen in `/proc/$pid/fd` (on Unix based systems).

