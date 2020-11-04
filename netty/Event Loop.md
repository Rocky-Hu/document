简言之就是JS只有一个主线程，主线程执行完执行栈的任务后去检查异步的任务队列，如果异步事件触发，则将其加到主线程的执行栈。

https://www.digitalocean.com/community/tutorials/understanding-the-event-loop-callbacks-promises-and-async-await-in-javascript



