https://developer.mozilla.org/zh-CN/docs/Glossary/CORS

https://stackoverflow.com/questions/27406994/http-requests-withcredentials-what-is-this-and-why-using-it

~~~
Short answer:

withCredentials() makes your browser include cookies and authentication headers in your XHR request. If your service depends on any cookie (including session cookies), it will only work with this option set.

Longer explanation:

When you issue an Ajax request to a different origin server, the browser may send an OPTIONS pre-flight request to the server to discover the CORS policy of the endpoint (for non-GET requests).

Since the request may have been triggered by a malicious script, to avoid automatically leaking authentication information to the remote server, the browser applies the following rules :

For GET requests, include cookie and authentication information in the server request :

if XHR client is invoked with the withCredentials option is set to true
and if the server reply does not include the CORS header Access-Control-Allow-Credentials: true, discard response before returning the object to Javascript
For non GET requests, include cookie and authentication information only:

if withCredentials is set to true on the XHR object
and the server has included the CORS header Access-Control-Allow-Credentials: true in the pre-flight OPTIONS
~~~

