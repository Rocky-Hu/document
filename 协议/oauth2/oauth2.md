https://tools.ietf.org/html/rfc6749

密码要分享个第三方、没法控制受限资源访问范围、无法回收权限（除了修改密码）。

# 一、介绍

## 1.1. 角色

资源服务(resource server)、资源拥有者(resource owner)、授权服务(authorization server)、第三方应用(third-party  application)。

## 1.2. 协议流程

```
     +--------+                               +---------------+
     |        |--(A)- Authorization Request ->|   Resource    |
     |        |                               |     Owner     |
     |        |<-(B)-- Authorization Grant ---|               |
     |        |                               +---------------+
     |        |
     |        |                               +---------------+
     |        |--(C)-- Authorization Grant -->| Authorization |
     | Client |                               |     Server    |
     |        |<-(D)----- Access Token -------|               |
     |        |                               +---------------+
     |        |
     |        |                               +---------------+
     |        |--(E)----- Access Token ------>|    Resource   |
     |        |                               |     Server    |
     |        |<-(F)--- Protected Resource ---|               |
     +--------+                               +---------------+
```

## 1.3. 授权批准（Authorization Grant）

资源拥有者批准第三方应用访问自有资源。有四种授权类型（颁发令牌的方式）：

授权码（authorization code）

隐式的（implicit）

资源拥有者的密码凭证（resource owner password credentials）

客户端凭证（client credentials）

> 以什么样的方式来获取access token。

### 1.3.1. 授权码（Authorization Code）

### 1.3.2. 隐式的（Implicit）

### 1.3.3. 资源拥有者密码凭证（Resource Owner Password Credentials）

### 1.3.4. 客户端凭证（Client Credentials）

## 1.4. 访问令牌（Access Token）

## 1.5. 刷新令牌（Refresh Token）

# 二、客户端注册

## 2.1. 客户端类型

## 2.2. 客户端标识

## 2.3. 客户端认证

# 三、协议Endpoints

## 3.1. Authorization Endpoint

授权码授权和隐式授权流程中会使用到Authorization Endpoint。

### 3.1.1. Response Type

客户端通过使用response_type请求参数来告知授权服务它需要的授权类型。参数的取值如下：

`code`: 请求一个授权码（授权码授权）

`token`: 请求一个access token（隐式授权）

### 3.1.2. Redirection Endpoint

与资源拥有者交互完成后，授权服务重定向。

## 3.2. Token Endpoint

## 3.3. Access Token Scope

## 四、Obtaining Authorization

## 4.1. Authorization Code Grant

### 4.1.1. 授权请求（Authorization Request）

授权请求中包含的参数有：

| 参数          | 必需        | 说明                        |
| ------------- | ----------- | --------------------------- |
| response_type | REQUIRED    | Value MUST be set to "code" |
| client_id     | REQUIRED    |                             |
| redirect_uri  | OPTIONAL    |                             |
| scope         | OPTIONAL    |                             |
| state         | RECOMMENDED |                             |

示例：

~~~
GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz
        &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
Host: server.example.com
~~~

### 4.1.2. 授权响应（Authorization Response）

| 参数  | 必需     | 说明                                                       |
| ----- | -------- | ---------------------------------------------------------- |
| code  | REQUIRED | 1. 有短期的存活时间（推荐10分钟）；2. 只允许使用一次       |
| state | REQUIRED | 如果授权请求中包含了state，那么就必须返回接收到的这个state |

示例：

~~~
HTTP/1.1 302 Found
Location: https://client.example.com/cb?code=SplxlOBeZQQYbYS6WxSbIA
               &state=xyz
~~~

#### 4.1.2.1. 错误响应（Error Response）

| 错误参数          | 必需     | 说明                                                         |
| ----------------- | -------- | ------------------------------------------------------------ |
| error             | REQUIRED | 参数值如下：invalid_request、unauthorized_client、access_denied、unsupported_response_type、invalid_scope、server_error、temporarily_unavailable |
| error_description | OPTIONAL |                                                              |
| error_uri         | OPTIONAL |                                                              |
| state             | REQUIRED | 如果授权请求中包含了state，那么就必须返回接收到的这个state   |

示例：

~~~
HTTP/1.1 302 Found
Location: https://client.example.com/cb?error=access_denied&state=xyz
~~~

### 4.1.3. 访问令牌请求（Access Token Request）

| 参数         | 必需     | 说明                     |
| ------------ | -------- | ------------------------ |
| grant_type   | REQUIRED | 值为：authorization_code |
| code         | REQUIRED | 从授权服务获取的授权码   |
| redirect_uri | REQUIRED |                          |
| client_id    | REQUIRED |                          |

示例：

~~~
POST /token HTTP/1.1
Host: server.example.com
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb
~~~

### 4.1.4. 访问令牌响应（Access Token Response）

成功响应示例：

~~~json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

{
    "access_token":"2YotnFZFEjr1zCsicMWpAA",
    "token_type":"example",
    "expires_in":3600,
    "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
    "example_parameter":"example_value"
}
~~~

## 4.2. Implicit Grant

### 4.2.1. 授权请求（Authorization Request）

| 参数          | 必需        | 说明          |
| ------------- | ----------- | ------------- |
| response_type | REQUIRED    | 取值为：token |
| client_id     | REQUIRED    |               |
| redirect_uri  | OPTIONAL    |               |
| scope         | OPTIONAL    |               |
| state         | RECOMMENDED |               |

示例：

~~~
GET /authorize?response_type=token&client_id=s6BhdRkqt3&state=xyz
     &redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
Host: server.example.com
~~~

### 4.2.2. 访问令牌响应（Access Token Response）

| 参数         | 必需        | 说明 |
| ------------ | ----------- | ---- |
| access_token | REQUIRED    |      |
| token_type   | REQUIRED    |      |
| expires_in   | RECOMMENDED |      |
| scope        | OPTIONAL    |      |
| state        | REQUIRED    |      |

示例：

~~~
HTTP/1.1 302 Found
Location: http://example.com/cb#access_token=2YotnFZFEjr1zCsicMWpAA
               &state=xyz&token_type=example&expires_in=3600

~~~

#### 4.2.1.1. 错误响应（Error Response）

| 参数              | 必需     | 说明 |
| ----------------- | -------- | ---- |
| error             | REQUIRED |      |
| error_description | OPTIONAL |      |
| error_uri         | OPTIONAL |      |
| state             | REQUIRED |      |

示例：

~~~
HTTP/1.1 302 Found
Location: https://client.example.com/cb#error=access_denied&state=xyz
~~~

## 4.3. Resource Owner Password Credentials Grant

### 4.3.1. 授权请求和响应（Authorization Request and Response）

### 4.3.2. 访问令牌请求（Access Token Request）

| 参数       | 必需     | 说明           |
| ---------- | -------- | -------------- |
| grant_type | REQUIRED | 取值：password |
| username   | REQUIRED |                |
| password   | REQUIRED |                |
| scope      | OPTIONAL |                |

示例：

~~~

POST /token HTTP/1.1
Host: server.example.com
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded

     grant_type=password&username=johndoe&password=A3ddj3w
~~~

### 4.3.3. 访问令牌响应（Access Token Response）

成功响应示例：

~~~json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

{
"access_token":"2YotnFZFEjr1zCsicMWpAA",
"token_type":"example",
"expires_in":3600,
"refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
"example_parameter":"example_value"
}
~~~

## 4.4. Client Credentials Grant

### 4.4.1. 授权请求和响应（Authorization Request and Response）

### 4.4.2. 访问令牌请求（Access Token Request）

| 参数       | 必需     | 说明                     |
| ---------- | -------- | ------------------------ |
| grant_type | REQUIRED | 取值：client_credentials |
| scope      | OPTIONAL |                          |

示例：

~~~
POST /token HTTP/1.1
Host: server.example.com
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials
~~~

### 4.4.3. 访问令牌响应（Access Token Response）

示例：

~~~
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

{
    "access_token":"2YotnFZFEjr1zCsicMWpAA",
    "token_type":"example",
    "expires_in":3600,
    "example_parameter":"example_value"
}
~~~

# 五、Issuing an Access Token

## 5.1. 成功的响应（Successful Response）

| 参数          | 必需        | 说明 |
| ------------- | ----------- | ---- |
| access_token  | REQUIRED    |      |
| token_type    | REQUIRED    |      |
| expires_in    | RECOMMENDED |      |
| refresh_token | OPTIONAL    |      |
| scope         | OPTIONAL    |      |

示例：

~~~json
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

{
    "access_token":"2YotnFZFEjr1zCsicMWpAA",
    "token_type":"example",
    "expires_in":3600,
    "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
    "example_parameter":"example_value"
}

~~~

## 5.2. 错误响应（Error Response）

| 参数              | 必需     | 说明                                                         |
| ----------------- | -------- | ------------------------------------------------------------ |
| error             | REQUIRED | 1. invalid_request                                                                                                                  2.invalid_client                                                                                                                              3.invalid_grant                                                                                                                     4.unauthorized_client                                                                                                 5.unsupported_grant_type                                                                                                   6.invalid_scope |
| error_description | OPTIONAL |                                                              |
| error_uri         | OPTIONAL |                                                              |

示例：

~~~json
HTTP/1.1 400 Bad Request
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache

{
    "error":"invalid_request"
}
~~~

# 六、Refreshing an Access Token

| 参数          | 必需     | 说明 |
| ------------- | -------- | ---- |
| grant_type    | REQUIRED |      |
| refresh_token | REQUIRED |      |
| scope         | OPTIONAL |      |

示例：

~~~json
POST /token HTTP/1.1
Host: server.example.com
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded

     grant_type=refresh_token&refresh_token=tGzv3JOkF0XG5Qx2TlKWIA
~~~

# 七、Accessing Protected Resources





