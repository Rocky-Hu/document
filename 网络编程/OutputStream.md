# 类说明

这个类是Java的基本输出流类。

这个类提供了写入数据所需的基本方法。这些方法包括：

~~~java
public abstract void write(int b) throws IOException
public void write(byte b[]) throws IOException
public void write(byte b[], int off, int len) throws IOException
public void flush() throws IOException
public void close() throws IOException
~~~

OutputStream的子类使用这些方法向某种特定的介质写入数据。例如，FileOutputStream使用这些方法将数据写入文件。TelnetOutputStream使用这些方法将数据写入网络连接。ByteArrayOutputStream使用这些方法将数据写入可扩展的字节数组。

# write(int b)

OutputStream的基本方法是write(int b)。这个方法接受一个0到255之间的整数作为参数，将对应的字节写入到输出流中。

**注意，**虽然这个方法接受一个int作为参数，但它实际上会写入一个无符号字节。Java没有无符号字节数据类型，所以这里要使用int来代替。无符号字节和有符号字节之间唯一的真正区别在与解释。它们都由8个二进制位。如果键给一个超过0~255的int传入write(int b)，将写入这个数的最低字节，其他3字节将被忽略（这正是将int强制转换为byte的结果）。

也就是说此方法此写入一个字节。

**实验：**

~~~java
FileOutputStream outputStream = new FileOutputStream(new File("/Users/rocky/Desktop/1.txt"));
outputStream.write(256);
outputStream.close();
~~~

上述代码传入的是整型数字256，256用二进制表示为00000000 00000000 00000001 00000000。因为此方法只会写入低8位，所以运行此代码，1.txt文件中的内容用16进制表示则为x00。

# write(byte b[]) 和 write(byte b[], int off, int len)

write(byte b[]) 内部调用的就是write(byte b[], int off, int len)方法。

~~~java
public void write(byte b[]) throws IOException {
    write(b, 0, b.length);
}
~~~

上面的write(int b)方法一次写入1个字节。一次写入1字节通常效率不高。例如，流出以太网卡的每个TCP分片包含至少40字节的开销用于路由和纠错。如果每字节都单独发送，那么与你预想的数据量相比，实际填入到网络中的数据可能会高出42倍以上！如果增加主机网络协议的开销，情况可能更糟糕。因此，大多数TCP/IP实现都会在某种程度上缓存数据。也就是说，它们在内存中积累数据字节，只有积累到一定量的数据后，或者经过一定的时间后，才将所积累的数据发送到最终目的地。不过，如果有多字节要发送，则一次全部发送不失为一个好主意。使用write(byte b[])和write(byte b[], int off, int len)通常比一次写入data数组中的1字节要快得多。

现在来看一下OutputStream中的write(byte b[], int off, int len)方法的源码：

~~~java
public void write(byte b[], int off, int len) throws IOException {
    if (b == null) {
        throw new NullPointerException();
    } else if ((off < 0) || (off > b.length) || (len < 0) ||
               ((off + len) > b.length) || ((off + len) < 0)) {
        throw new IndexOutOfBoundsException();
    } else if (len == 0) {
        return;
    }
    for (int i = 0 ; i < len ; i++) {
        write(b[off + i]);
    }
}
~~~

从源码中我们可以看到，实际上次方法还是会调用write(int b)方法，既然内部依然调用的是写一个字节的方法，那么上面说得比写入一个字节要快得多如何说起呢？来看看这个方法的注释的一段说明：

> The write method of OutputStream calls the write method of one argument on each of the bytes to be written out. Subclasses are encouraged to override this method and provide a more efficient implementation.

上面的注释说得很清楚了：这个方法中每个字节的写出是调用的接收一个参数的write的方法，但是鼓励子类重写这个方法来提供更高效的操作。

FileOutputStream是OutputStream的一个子类，我们可以看到这个方法重重写了write(byte b[])：

~~~java
public void write(byte b[]) throws IOException {
    writeBytes(b, 0, b.length, append);
}

private native void writeBytes(byte b[], int off, int len, boolean append)
        throws IOException;
~~~

最终调用的是一个本地方法。下面是这个本地方法的代码：

~~~java
void
writeBytes(JNIEnv *env, jobject this, jbyteArray bytes,
           jint off, jint len, jboolean append, jfieldID fid)
{
    jint n;
    char stackBuf[BUF_SIZE];
    char *buf = NULL;
    FD fd;

    // 判断Java传入的byte数组是否是null
    if (IS_NULL(bytes)) {
        JNU_ThrowNullPointerException(env, NULL);
        return;
    }

    // 判断off和len参数是否数组越界
    if (outOfBounds(env, off, len, bytes)) {
        JNU_ThrowByName(env, "java/lang/IndexOutOfBoundsException", NULL);
        return;
    }

    // 如果写入长度为0，直接返回0
    if (len == 0) {
        return;
    } else if (len > BUF_SIZE) {
        // 如果写入长度大于BUF_SIZE（8192），无法使用栈空间buffer
        // 需要调用malloc在堆空间申请buffer
        buf = malloc(len);
        if (buf == NULL) {
            JNU_ThrowOutOfMemoryError(env, NULL);
            return;
        }
    } else {
        buf = stackBuf;
    }

    // 复制Java传入的byte数组数据到C空间的buffer中
    (*env)->GetByteArrayRegion(env, bytes, off, len, (jbyte *)buf);

    if (!(*env)->ExceptionOccurred(env)) {
        off = 0;
        while (len > 0) {
            // 获取记录在FileDescriptor中的文件描述符
            fd = GET_FD(this, fid);
            if (fd == -1) {
                JNU_ThrowIOException(env, "Stream Closed");
                break;
            }

            // 追加模式和普通模式使用不同的函数
            if (append == JNI_TRUE) {
                n = IO_Append(fd, buf+off, len);
            } else {
                n = IO_Write(fd, buf+off, len);
            }
            if (n == -1) {
                JNU_ThrowIOExceptionWithLastError(env, "Write error");
                break;
            }
            off += n;
            len -= n;
        }
    }
    if (buf != stackBuf) {
        free(buf);
    }
}
~~~

上面可以看到有块内存区域(buf)作为缓冲区，然后与OutputStream的类似将字节数据存放到这块内存中。拿写内容到文件中为例，将内容写入文件需要磁盘的I/O操作，磁盘读写是系统调用，会从用户态切换到内核态，频繁的切换会导致性能的损失。如果每写一个字节就执行磁盘的I/O操作，那么性能低下。这里是先把内容写入到内存中，然后再一次性得将这块内存的内容执行硬盘I/O操作写入到文件中，这样减少了磁盘的I/O操作，从而提升了性能。

> IO_Write方法最终调用的是操作系统底层的方法，也就是涉及到系统调用。具体内容可以查看此帖：[https://stackoverflow.com/questions/32923184/java-write-operation-io-append-io-write](https://links.jianshu.com/go?to=https%3A%2F%2Fstackoverflow.com%2Fquestions%2F32923184%2Fjava-write-operation-io-append-io-write)

上面的代码中还有一个比较有意思的地方：关于buf指针类型参数的赋值问题，上面的赋值是通过if-else来操作的。

~~~java
if (len == 0) {
    return;
} else if (len > BUF_SIZE) {
    buf = malloc(len);
    if (buf == NULL) {
        JNU_ThrowOutOfMemoryError(env, NULL);
        return;
    }
} else {
    buf = stackBuf;
}
~~~

这里来看一下BUF_SIZE的定义：

~~~c
/* The maximum size of a stack-allocated buffer.
 */
#define BUF_SIZE 8192
~~~

堆栈分配缓冲区的最大大小。上面的代码的逻辑是这样的：如果当前要写入的字节数组的大小小于或等于8192，那么直接用栈中已分配的缓存区域来接收字节数组；如果大于，栈中分配的缓冲区内存无法容纳要写入的字节数组，那么就通过malloc方法申请一块内存，在方法末尾调用free方法来释放这块内存。而我们知道申请和释放内存是一个耗时的操作，那么在实际操作的过程中我们控制（每次）要写入的的字节数组大小，使其不大于8192，那么就可以直接使用栈中的缓冲区从而避免申请和释放内存，从而提高性能（下节要描述的内容也会涉及到这个8192的内容）。

# flush()

要避免频繁得I/O操作，像上面一样先将数据写入到内存中的某一块区域中，然后再在合适的情况下执行I/O操作，在块区域就是所谓的缓冲区。通过这个缓冲区来减小对磁盘读写（以文件读写为例）的压力。

上面的代码中通过buf = malloc(len)分配了一个缓冲区，这个操作是在本地方法中实现的，对使用Java API的人员透明。同样的在Java代码中也可以类似地像这样实现自己的缓存区。BufferedOutputStream就提一个带缓冲区的输出流实现类。

**第一个测试方法：**

~~~java
public class BufferedOutputStreamTest {

    @Test
    public void test1() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("d://1.txt"));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        for (int i =0;i<10;i++) {
            bufferedOutputStream.write(i);
        }
    }

}
~~~

运行上面的方法，观察结果，会发现文件中无任何内容写入。

**第二个测试方法：**

~~~java
@Test
public void test2() throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(new File("d://1.txt"));
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

    for (int i =0;i<10;i++) {
        bufferedOutputStream.write(i);
    }

    bufferedOutputStream.flush();
}
~~~

运行test2方法，观察结果，会发现文件中有内容写入。test2和test1唯一不同的就是代码最后调用了flush方法。

在看flush之前我们先来看一下BufferedOutputStream的write方法，方法源码如下：

~~~java
public synchronized void write(int b) throws IOException {
    if (count >= buf.length) {
        flushBuffer();
    }
    buf[count++] = (byte)b;
}
~~~

可以与FileOutputStream的write方法做一下对比，调用FileOutputStream的write方法最终会调用writeBytes这个本地方法，这个本地方法就涉及到磁盘的I/O操作，也就是真实得向文件写数据。而上面的write方法，我们可以看到只是把字节放入到预先定义好的字节数组中，这个字节数组就是内存中的一块区域，也就是我们所说的缓冲区。因为不涉及到磁盘的I/O操作，所以运行test1是不会真实得向文件写数据的。

那为什么执行flush之后就能真实得写了呢？看一下flush的源码：

~~~java
public synchronized void flush() throws IOException {
    flushBuffer();
    out.flush();
}

/** Flush the internal buffer */
private void flushBuffer() throws IOException {
    if (count > 0) {
        out.write(buf, 0, count);
        count = 0;
    }
}
~~~

调用flush方法会调用内部的flushBuffer方法，这里可以看到flushBuffer内部有这样调用：

~~~java
out.write(buf, 0, count);
~~~

这个out就是我们上面定义的fileOutputStream，执行FileOutputStream的这个wirte方法，最终会调用writeBytes这个本地方法，执行磁盘的I/O操作。所以执行test2后，内容被写到了文件中。

**第三个测试方法：**

~~~java
@Test
public void test3() throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(new File("d://1.txt"));
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

    for (int i =0;i<8193;i++) {
        bufferedOutputStream.write(i);
    }

}
~~~

执行此方法，观察结果会发现，文件有内容写入。这里没有调用flush方法，数据为什么就写入到文件中了呢？还是来看write方法：

~~~java
public synchronized void write(int b) throws IOException {
    if (count >= buf.length) {
        flushBuffer();
    }
    buf[count++] = (byte)b;
}
~~~

代码中清晰得表达了，当写入的字节数大于缓存数组的长度的时候就会自动调用flushBuffer方法。BufferedOutputStream默认的缓冲区数组长度为8192个字节，也就是8kb。上面我们执行了8193次循环，会触发一次自动刷新缓冲区的操作。

**注意：**执行bufferedOutputStream.write(8192)时触发刷新操作，但是8192这个数字的低8位是不会写入的，而是会执行buf[count++] = (byte)b，同时现在的count的值是0的，因为flushBuffer会将count重设为0。

> 执行一次缓冲区刷新操作之后缓冲区里面的数据需要清空吗？答案是不需要。执行缓存区刷新之后，count会被设置为0，那么新写入的数据会重新从数组的起始位置开始依次存入。同时刷新缓冲区，执行缓冲字节的写入操作是与这个count有关系的：out.write(buf, 0, count)，这里定义了数组取值的边界。所以刷新缓冲区不需要清空缓冲区内的数据，而是采用覆盖方式来重新存放新数据。

**补充：**BufferedOutputStream中默认的缓冲区字节数组大小为什么要定义为8192，结合上一节的内容就可以知道答案。

# close()

BufferedOutputStreamTest中列出的几个例子其实写法上是有问题的，在我们刚学习Java I/O的时候，老师或者相关资料上都会告诉我们在我们获取输入或输出流对象进行操作完毕后，要调用close方法来关闭与流对象关联的系统资源。

这个系统资源是什么？

i-node节点包含了文件的元数据信息，可以说是文件的代表者；全局的文件表表示已打开的文件，文件表记录与i-node有关联；进程表中包含了文件描述符记录，该记录有指向文件表具体记录项的指针。通过文件描述符我们就可以间接地操作要操作的文件。

文件必须被打开才能被访问。文件可以以只读方式或者只写方式的打开，或者两者兼有。一个打开的文件通过唯一的文件描述符进行引用，该描述符是打开文件的元数据至其本身的映射。

程序完成对某个文件的操作后，可以时候close()系统调用将文件描述符和对应的文件解除关联。close()调用解除了已打开的文件描述符的关联，并分离进程和文件的关联。给定的文件描述符不再有效，内核可以随意将其作为随后的open()或creat()调用的返回值而重新使用。

接下来从源码的角度来看一下打开文件和关闭文件具体做了哪些工作：

## **打开文件**

~~~java
File file = new File("D:/1.txt");
FileOutputStream fileOutputStream = new FileOutputStream(file);
FileDescriptor fileDescriptor = fileOutputStream.getFD();
~~~

上面创建了一个FileOutputStream对象，然后获取内部的FileDescriptor属性，调试代码可以看到fileDescriptor是非null的，说明我们获得了一个文件描述符。

FileOutputStream的构造方法中对fileDescriptor进行了设置：

~~~java
this.fd = new FileDescriptor();
fd.attach(this);
~~~

涉及到FileDescriptor的只有这两行代码。FileDescriptor对象的其他属性是怎么设置的呢？FileOutputStream两个参数的构造函数的最后一行会触发本地open0方法的调用。

~~~java
// wrap native call to allow instrumentation
/**
 * Opens a file, with the specified name, for overwriting or appending.
 * @param name name of file to be opened
 * @param append whether the file is to be opened in append mode
 */
private void open(String name, boolean append)
    throws FileNotFoundException {
    open0(name, append);
}

/**
 * Opens a file, with the specified name, for overwriting or appending.
 * @param name name of file to be opened
 * @param append whether the file is to be opened in append mode
 */
private native void open0(String name, boolean append)
    throws FileNotFoundException;
~~~

本地方法open的定义如下：

*jdk/src/solaris/native/java/io/FileOutputStream_md.c*

```cpp
JNIEXPORT void JNICALL
Java_java_io_FileOutputStream_open(JNIEnv *env, jobject this,
                                   jstring path, jboolean append) {
    fileOpen(env, this, path, fos_fd,
             O_WRONLY | O_CREAT | (append ? O_APPEND : O_TRUNC));
}
```

注意这里的fos_fd参数。这个参数有什么作用呢？FileOutputStream_md.c中作了如下定义：

```bash
jfieldID fos_fd; /* id for jobject 'fd' in java.io.FileOutputStream */
```

这里可以看到跟FileOutputStream中的FileDescriptor类型的fd属性有关。

继续看这个参数的初始化：

```dart
/**************************************************************
 * static methods to store field ID's in initializers
 */

JNIEXPORT void JNICALL
Java_java_io_FileOutputStream_initIDs(JNIEnv *env, jclass fdClass) {
    fos_fd = (*env)->GetFieldID(env, fdClass, "fd", "Ljava/io/FileDescriptor;");
}
```

这里是本地方法initIDs的定义，FileOutputStream中相对应的定义如下：

```java
private static native void initIDs();

static {
    initIDs();
}
```

当FileOutputStream类被加载的时候就会执行initIDs方法。

**总结: fos_fd参数代表的是FileOutputStream类对象的fd属性。**

*jdk/src/solaris/native/java/io/io_util_md.h*

```cpp
/*
 * Macros to use the right data type for file descriptors
 */
#define FD jint

FD handleOpen(const char *path, int oflag, int mode);
```

*jdk/src/solaris/native/java/io/io_util_md.c*

```cpp
FD handleOpen(const char *path, int oflag, int mode) {
    FD fd;
    RESTARTABLE(open64(path, oflag, mode), fd);
    if (fd != -1) {
        struct stat64 buf64;
        int result;
        RESTARTABLE(fstat64(fd, &buf64), result);
        if (result != -1) {
            if (S_ISDIR(buf64.st_mode)) {
                close(fd);
                errno = EISDIR;
                fd = -1;
            }
        } else {
            close(fd);
            fd = -1;
        }
    }
    return fd;
}

void
fileOpen(JNIEnv *env, jobject this, jstring path, jfieldID fid, int flags)
{
    WITH_PLATFORM_STRING(env, path, ps) {
        FD fd;

#if defined(__linux__) || defined(_ALLBSD_SOURCE)
        /* Remove trailing slashes, since the kernel won't */
        char *p = (char *)ps + strlen(ps) - 1;
        while ((p > ps) && (*p == '/'))
            *p-- = '\0';
#endif
        fd = handleOpen(ps, flags, 0666);
        if (fd != -1) {
            SET_FD(this, fd, fid);
        } else {
            throwFileNotFoundException(env, path);
        }
    } END_PLATFORM_STRING(env, ps);
}
```

从上面可以看到成功执行handleOpen后，会得到一个文件描述符FD，我们现在讨论的是FileOutputStream中FileDescriptor对象fd的属性的设值过程，那么我们可以猜测得到这个整型的文件描述符后，肯定会设置到fd对象中，设置的代码如下：

```kotlin
if (fd != -1) {
    SET_FD(this, fd, fid);
} else {
    throwFileNotFoundException(env, path);
}
```

文件描述符为-1，表示要打开的文件在系统中不存在，这里抛出了FileNotFoundException；非-1进行设值。这里的fid就是我们上面分析的FileOutputStream的fd属性。

SET_FD方法的定义如下：

*jdk/src/solaris/native/java/io/io_util_md.h*

```kotlin
#define SET_FD(this, fd, fid) \
    if ((*env)->GetObjectField(env, (this), (fid)) != NULL) \
        (*env)->SetIntField(env, (*env)->GetObjectField(env, (this), (fid)),IO_fd_fdID, (fd))
```

**总结：通过FileOutputStream的构造函数构造对象，内部会调用本地方法打开文件，底层代码打开文件会返回该打开文件对应的文件描述符，然后会将这个整型的文件描述符设值到FileOutputStream的FileDescriptor对象的fd属性中。**

## 关闭文件

还是拿FileOutputStream为例，关闭文件最终会调用本地close方法。

*jdk/src/solaris/native/java/io/FileOutputStream_md.c*

```cpp
JNIEXPORT void JNICALL
Java_java_io_FileOutputStream_close0(JNIEnv *env, jobject this) {
    fileClose(env, this, fos_fd);
}
```

*jdk/src/solaris/native/java/io/io_util_md.c*

```kotlin
void
fileClose(JNIEnv *env, jobject this, jfieldID fid)
{
    FD fd = GET_FD(this, fid);
    if (fd == -1) {
        return;
    }

    /* Set the fd to -1 before closing it so that the timing window
     * of other threads using the wrong fd (closed but recycled fd,
     * that gets re-opened with some other filename) is reduced.
     * Practically the chance of its occurance is low, however, we are
     * taking extra precaution over here.
     */
    SET_FD(this, -1, fid);

    /*
     * Don't close file descriptors 0, 1, or 2. If we close these stream
     * then a subsequent file open or socket will use them. Instead we
     * just redirect these file descriptors to /dev/null.
     */
    if (fd >= STDIN_FILENO && fd <= STDERR_FILENO) {
        int devnull = open("/dev/null", O_WRONLY);
        if (devnull < 0) {
            SET_FD(this, fd, fid); // restore fd
            JNU_ThrowIOExceptionWithLastError(env, "open /dev/null failed");
        } else {
            dup2(devnull, fd);
            close(devnull);
        }
    } else if (close(fd) == -1) {
        JNU_ThrowIOExceptionWithLastError(env, "close failed");
    }
}
```

从代码中可以看到，最终调用的是系统有关的close方法。这涉及到系统调用，而传入的参数为文件描述符。

以linux系统的close为例，close方法的作用如下：

> close() closes a file descriptor, so that it no longer refers to any
> file and may be reused. Any record locks (see fcntl(2)) held on the
> file it was associated with, and owned by the process, are removed
> (regardless of the file descriptor that was used to obtain the lock).

close()关闭文件描述符，使其不再引用任何文件，以可以重用。保存在与其关联的文件上的任何记录锁和进程所拥有的记录锁都将被删除。