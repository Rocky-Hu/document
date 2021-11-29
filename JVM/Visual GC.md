# 一、Graph Window

## Compile Time

![](../images/jvm/Visual GC_Complie Time.png)

编译时间表示虚拟机的 JIT 编译器编译热点代码的耗时。

Java 语言为了实现跨平台特性， Java 代码编译出来后形成的 class 文件中存储的是 byte code，jvm 通过解释的方式形成字节码命令，这种方式与 C/C++ 编译成二进制的方式相比要慢不少。

为了解决程序解释执行的速度问题， jvm 中内置了两个运行时编译器，如果一段 Java 代码被调用达到一定次数，就会判定这段代码为热点代码（hot spot code），并将这段代码交给 JIT 编译器编译成本地代码，从而提高运行速度。所以随着代码被编译的越来越彻底，运行速度应当是越来越快。

而 Java 运行器编译的最大缺点就是它进行编译时需要消耗程序正常的运行时间，也就是 compile time.

## Class Loader Time

![](../images/jvm/Visual GC_Class Loader Time.png)

表示 class 的 load 和 unload 时间。

## GC Time

![](../images/jvm/Visual GC_GC Time.png)

3 collections 表示自监视以来一共经历了 3次GC, 包括 Minor GC 和 Full GC

47.225ms表示 gc 共花费了47.225ms

Last Cause（表示上次发生 gc 的原因）：G1 Evacuation Pause 

## Eden Space

![](../images/jvm/Visual GC_Eden Space.png)

### Eden Space (6.000G,416.000M): 294.000M

6.000G：表示Eden Space最大可分配空间

416.000M：表示Eden Space当前分配空间

294.000M：表示Eden Space当前占用空间

### 3 collections, 47.225ms

表示当前新生代发生 GC 的次数为 3 次, 共占用时间47.225ms

## Survivor 0 and Survivor 1

![](../images/jvm/Visual GC_S0 S1.png)

S0 和 S1 肯定有一个是空闲的，这样才能方便执行 minor GC 的操作，但是两者的最大分配空间是相同的。并且在 minor GC 时，会发生 S0 和S1 之间的切换。

### Survivor 1（6.000G，22.000M）：22.000M

表示S1最大可分配空间为6.000G，当前分配空间为22.000M，已占用空间22.000M。

## Old Gen

![](../images/jvm/Visual GC_Old Gen.png)

Old Gen（6.000G，4.098G）：0, 0 collections，0s

表示Old Gen最大分配空间6.000G, 当前空间4.098G，已占用0。老年代共发生了0次GC，耗费0s。

老年代 GC 也叫做 Full GC， 因为在老年代 GC 时总是会伴随着 Minor GC， 合起来就称为 Full GC。

## Metaspace

![](../images/jvm/Visual GC_Metaspace.png)

​    对 HotSpot 虚拟机来说，可以把永久代直接等同于方法区，其中会存储已经被jvm 加载的类信息，常量，静态变量，即时编译器编译后的代码等数据。