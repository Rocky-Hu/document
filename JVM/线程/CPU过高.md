# Why Do Java Applications Take High CPU?

Java applications may take high CPU resources for many reasons:

- Poorly designed application code with inefficient loops: Recursive method calls, inefficient usage of collections (e.g., excessively large ArrayLists instead of say using HashMaps) can also be reasons for this.
- Shortage of memory in the [Java Virtual Machine (JVM)](https://www.eginnovations.com/jvm-monitoring) can also reflect in high CPU usage. Instead of spending time in processing, the JVM spends more time in Garbage Collection, which in turn takes up CPU cycles.
- A JVM may max out on CPU usage because of the incoming workload. The server capacity may not be sized sufficiently to handle the rate of requests coming in and in such a situation, the Java application may be doing work, trying to keep up with the workload.