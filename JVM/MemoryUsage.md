A `MemoryUsage` object represents a snapshot of memory usage. Instances of the `MemoryUsage` class are usually constructed by methods that are used to obtain memory usage information about individual memory pool of the Java virtual machine or the heap or non-heap memory of the Java virtual machine as a whole.

A `MemoryUsage` object contains four values:

| `init`      | represents the initial amount of memory (in bytes) that the Java virtual machine requests from the operating system for memory management during startup. The Java virtual machine may request additional memory from the operating system and may also release memory to the system over time. The value of `init` may be undefined. |
| ----------- | ------------------------------------------------------------ |
| `used`      | represents the amount of memory currently used (in bytes).   |
| `committed` | represents the amount of memory (in bytes) that is guaranteed to be available for use by the Java virtual machine. The amount of committed memory may change over time (increase or decrease). The Java virtual machine may release memory to the system and `committed` could be less than `init`. `committed` will always be greater than or equal to `used`. |
| `max`       | represents the maximum amount of memory (in bytes) that can be used for memory management. Its value may be undefined. The maximum amount of memory may change over time if defined. The amount of used and committed memory will always be less than or equal to `max` if `max` is defined. A memory allocation may fail if it attempts to increase the used memory such that `used > committed` even if `used <= max` would still be true (for example, when the system is low on virtual memory). |

Below is a picture showing an example of a memory pool:

```
        +----------------------------------------------+
        +////////////////           |                  +
        +////////////////           |                  +
        +----------------------------------------------+

        |--------|
           init
        |---------------|
               used
        |---------------------------|
                  committed
        |----------------------------------------------|
                            max
```