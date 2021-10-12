# 一、[How to use "HttpRequest", "FullHttpRequest", "HttpMessage", "FullHttpMessage" and "LastHttpContent"?](https://stackoverflow.com/questions/29213793/how-to-use-httprequest-fullhttprequest-httpmessage-fullhttpmessage-an)

https://stackoverflow.com/questions/29213793/how-to-use-httprequest-fullhttprequest-httpmessage-fullhttpmessage-an

When an HTTP message is decoded by an `HttpObjectDecoder`, the decoder produces the following objects:

1. An `HttpRequest` or an `HttpResponse` that provides the properties decoded from the initial line and its following headers.
2. A series of `HttpContent`. The last `HttpContent` is `LastHttpContent`.

A typical handler code will look like the following:

```
if (msg instanceof HttpRequest) {
    ...
}
if (msg instanceof HttpContent) {
    ...
    if (msg instanceof LastHttpContent) {
        ...
    }
}
```

Please note that the `if` blocks are not mutually exclusive and thus the handler does not return when one of the 3 conditions above is met. Take a look into [HttpSnoopServerHandler](https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/http/snoop/HttpSnoopServerHandler.java#L60) for a concrete example.

Now, let's take a look at `FullHttpRequest`. It implements `HttpRequest`, `HttpContent`, and `LastHttpContent`. The well-written handler should just work when we inserted `HttpObjectAggregator` into the pipeline without changing any code.

So, the intention of this weird-looking class hierarchy is to enable a user to choose to use `HttpObjectAggregator` or not.

However, I do agree this is not intuitive. We are going to fix this in Netty 5 in such a way that the decoder produces only a single HTTP message object and streaming its content to it later.

# 二、[Can a single netty bootstrap connect to multiple hosts](https://stackoverflow.com/questions/8568708/can-a-single-netty-bootstrap-connect-to-multiple-hosts)

https://stackoverflow.com/questions/8568708/can-a-single-netty-bootstrap-connect-to-multiple-hosts

Yes you can reuse the client bootstrap without a problem. If you specify a ChannelPipelineFactory each new channel will get its own ChannelPipeline. If you want to have different settings for different clients you may just create one client bootstrap per connection. This works out very well as the bootstrap is really light-weight.

