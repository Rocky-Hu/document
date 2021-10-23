HTTPS将HTTP协议与一组强大的对称、非对称和基于证书的加密技术结合在一起，使得HTTPS不仅很安全，而且很灵活，很容易在处于无序状态的、分散的全球互联网上进行管理。

# SSL 证书级别

分为三种类型，域名型SSL证书（DV SSL）、企业型SSL证书（OVSSL）、增强型SSL证书（EVSSL）。

**1. 域名型 SSL 证书（DV SSL -** Domain Validation SSL**）**

即证书颁布机构只对域名的所有者进行在线检查，通常是验证域名下某个指定文件的内容，或者验证与域名相关的某条 TXT 记录；

比如访问 [http|https]://www.mimvp.com/.../test.txt，文件内容： 2016082xxxxxmimvpcom2016

或添加一条 TXT 记录：www.mimvp.com –> TXT –> 20170xxxxxmimvpcom2066

**2. 企业型 SSL 证书（OV SSL -** Organization Validation SSL**）**

是要购买者提交组织机构资料和单位授权信等在官方注册的凭证，

证书颁发机构在签发 SSL 证书前，不仅仅要检验域名所有权，

还必须对这些资料的真实合法性进行多方查验，只有通过验证的才能颁发 SSL 证书。

**3. 增强型 SSL 证书（EV SSL -** Extended Validation SSL**）**

与其他 SSL 证书一样，都是基于 SSL/TLS 安全协议，

但是验证流程更加具体详细，验证步骤更多，

这样一来证书所绑定的网站就更加的可靠、可信。

它跟普通 SSL 证书的区别也是明显的，安全浏览器的地址栏变绿，

如果是不受信的 SSL 证书则拒绝显示，如果是钓鱼网站，地址栏则会变成红色，以警示用户。






























