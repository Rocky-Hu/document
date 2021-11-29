The primary responsibility of the `TrustManager` is to determine whether the presented authentication credentials should be trusted. If the credentials are not trusted, then the connection will be terminated. To authenticate the remote identity of a secure socket peer, you must initialize an `SSLContext` object with one or more `TrustManager` objects. You must pass one `TrustManager` for each authentication mechanism that is supported. If null is passed into the `SSLContext` initialization, then a trust manager will be created for you. Typically, a single trust manager supports authentication based on X.509 public key certificates (for example, `X509TrustManager`). Some secure socket implementations may also support authentication based on shared secret keys, Kerberos, or other mechanisms.

`TrustManager` objects are created either by a `TrustManagerFactory`, or by providing a concrete implementation of the interface.

**TrustManager：确定是否应信任远程身份验证凭据（以及连接）。**

**KeyManager：确定要发送到远程主机的身份验证凭据。**

