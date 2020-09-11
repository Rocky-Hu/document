歧义在“TCP”上，这个“粘包”跟TCP其实没关系。

TCP保证的是数据流正确传输的机制，不管应用端怎么去用这个数据。

这里的“粘包”其实是应用程序中没有处理好数据包分割，两个应用层的数据包粘在一块了。

TCP说：我只是搬运，可不帮你装箱，这个锅我不背。

https://draveness.me/whys-the-design-tcp-message-frame/

