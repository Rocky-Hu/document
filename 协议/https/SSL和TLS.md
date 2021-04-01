安全传输层协议（TLS）用于在两个通信应用程序之间提供保密性和数据完整性。

SSL/TLS协议可分为两层：记录协议（Record Protocol）和握手协议（Handshake Protocol）。

- 记录协议（Record Protocol）：建立在可靠的传输协议（如TCP）之上，为高层协议提供数据封装、压缩、加密等基本功能的支持。
- 握手协议（Handshake Protocol）：建立在SSL记录协议之上，用于在实际的数据传输开始前，通讯双方进行身份认证、协商加密算法、交换加密密钥等。

SSL/TLS协议涉及多种加密算法，包含消息摘要算法、对称加密算法、非对称加密算法，以及数字签名算法。

- 消息摘要算法：MD5和SHA1。
- 对称加密算法：RC2、RC4、IDEA、DES、Triple DES和AES。
- 非对称加密算法：RAS和Diffie-Hellman（DH）。
- 数字签名算法：RSA和DSA。

# 一、交互流程

## 1.1. 协商算法

## 1.2. 验证证书

## 1.3. 产生密钥

## 1.4. 加密交互

