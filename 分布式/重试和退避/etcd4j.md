处理重试，需要定义好重试策略，此篇文章以etcd4j中的处理为例，来讲解重试机制。

# 一、RetryPolicy

## 1.1. RetryNTimes

重试N次。

## 1.2. RetryOnce

重试1次。

## 1.3. RetryWithTimeout

指定超时时间内重试，超时时间到后就不再重试。

## 1.4. RetryWithExponentialBackOff

有回退指数的重试。

