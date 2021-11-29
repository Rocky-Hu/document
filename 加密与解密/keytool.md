# 导入证书到cacerts

~~~
C:\Program Files\Java\jdk1.8.0_301\jre\lib\security>keytool -import -alias lqcert -keystore ./cacerts -file d:\\LQ.cer
输入密钥库口令:
再次输入新口令:
所有者: CN=*.longcheer.com
发布者: C=SK, O="ESET, spol. s r. o.", CN=ESET SSL Filter CA
序列号: 20eb85e3fb2c3a53ef5a0d60f01f5437
生效时间: Tue Jun 08 11:40:47 CST 2021, 失效时间: Sun Jul 10 11:40:47 CST 2022
证书指纹:
         SHA1: 1B:42:B7:04:8B:AD:72:4E:E1:27:53:76:0F:F7:1D:2D:44:AE:C1:D0
         SHA256: 3A:77:E9:AD:41:4D:3E:86:B7:B5:48:A0:D5:3F:20:39:37:EE:09:5E:4B:0B:4E:D7:B0:1E:94:CA:7E:ED:A2:16
签名算法名称: SHA256withRSA
主体公共密钥算法: 2048 位 RSA 密钥
版本: 3

扩展:

#1: ObjectId: 2.5.29.35 Criticality=false
AuthorityKeyIdentifier [
KeyIdentifier [
0000: ED 42 13 76 88 F0 7B 60   A4 87 C1 B8 C1 D7 26 6A  .B.v...`......&j
0010: D7 C6 BD CF                                        ....
]
]

#2: ObjectId: 2.5.29.19 Criticality=false
BasicConstraints:[
  CA:false
  PathLen: undefined
]

#3: ObjectId: 2.5.29.37 Criticality=false
ExtendedKeyUsages [
  serverAuth
  clientAuth
]

#4: ObjectId: 2.5.29.15 Criticality=false
KeyUsage [
  DigitalSignature
  Key_Encipherment
]

#5: ObjectId: 2.5.29.17 Criticality=false
SubjectAlternativeName [
  DNSName: *.longcheer.com
  DNSName: longcheer.com
]

#6: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: 13 3C 3C E0 DA 0C E9 72   D9 90 11 9F 64 ED 8C 04  .<<....r....d...
0010: D3 99 3E 01                                        ..>.
]
]

是否信任此证书? [否]:  是
证书已添加到密钥库中
~~~

# 查看keystore内容

~~~
keytool -list -keystore ./cacerts
~~~

# 删除证书

~~~
keytool -delete -alias akazam_email -keystore cacerts
~~~

