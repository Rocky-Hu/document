# [Java - Is ByteArrayOutputStream safe without flush() and close()?](https://stackoverflow.com/questions/23164598/java-is-bytearrayoutputstream-safe-without-flush-and-close)

Yes. It is safe to not `flush()` or not `close()` a `ByteArrayOutputStream`. and it makes no difference to memory usage whether you do or don't.

The only cases where `close()` or `flush()`1 do anything in connection with a `ByteArrayOuputStream` is if you have used it at the end of an output pipeline that includes a buffering component; e.g. a `BufferedWriter`. Then you *do* need to flush or close ... from the "top" of the pipeline ... to ensure that all of the data makes it into the byte array.

There are no GC implications for calling `flush()` or `close()`. Either way, the stream's content will continue to be held in memory for as long as the object remains reachable. (By contrast, streams the read / write to external resources need to be closed in a timely fashion, because they have an external "resource descriptor" that needs to be freed ...)

In summary:

- It does no harm to `flush()` or `close()` a bare `ByteArrayOutputStream`. It is just unnecessary.
- It is often *necessary* to close an output pipeline that ends in a `ByteArrayOutputStream`, but this is not because of memory usage or GC considerations.
- Memory is used (at least) as long as the `ByteArrayOutputStream` object is reachable.