# inode

在描述FileDescriptor之前先来看一下inode。inode在我们后面的描述中会出现，所以首先需要弄清楚它是什么。

inode是index node的简写。它是Unix风格文件系统中的一种数据结构，用来描述比如说文件或目录这样的文件系统对象。每个inode存储对象数据的属性和磁盘块位置。文件系统对象属性可以包括元数据(上次更改的时间、访问、修改)以及所有者和权限数据。

inode中存储的是文件的信息，例如文件所有权、访问模式(读、写、执行权限)和文件类型等。文件的真实数据不存储在inode中，而是存储在称为"数据块"的地方；同时文件的名称也不存储在inode中。在许多类型的文件系统实现中，在创建文件系统时都会固定inode的最大数量，从而限制了文件系统可以容纳的最大文件数。对于文件系统中的inode，典型的分配启发式是总大小的1%。

每个文件都与inode相关联，inode由整数标识，通常称为i-number或inode号。

在设备的已知区域有一张inode表，inode编号就是这个表的索引。根据inode编号，内核的文件系统驱动程序可以访问inode内容，包括文件的位置，从而允许访问文件。

使用ls -i命令可以找到文件的inode号。ls-i命令在报表的第一列中打印 i-node编号。

~~~verilog
rockydeMacBook-Pro:~ rocky$ ls -i
14373725 Applications       624053 Movies           14740502 account.txt
624013 Desktop              624055 Music            3800781 default-soapui-workspace.xml
623997 Documents            624057 Pictures         4320031342 node_modules
623999 Downloads            624059 Public           3800780 soapui-settings.xml
624001 Library              670752 Work
~~~

如果知道了文件的inode编号，那么可以使用下面的命令来查找文件：

~~~verilog
rockydeMacBook-Pro:Work rocky$ ls -i
13583397 config_datasource.properties   13964509 npm-debug.log          13175726 workspace
2913703 document              674806 project
13822742 idea                 670756 software
rockydeMacBook-Pro:Work rocky$ find . -inum 13583397 -print
./config_datasource.properties
rockydeMacBook-Pro:Work rocky$ 
~~~

还可以根据文件的inode编号来删除文件：

~~~verilog
rockydeMacBook-Pro:Work rocky$ touch 1.txt
rockydeMacBook-Pro:Work rocky$ ls
1.txt               document            npm-debug.log           software
config_datasource.properties    idea                project             workspace
rockydeMacBook-Pro:Work rocky$ ls -li
total 32
4321587203 -rw-r--r--   1 rocky  staff     0 Apr 29 20:41 1.txt
13583397 -rwxrwxrwx@  1 rocky  staff   138 Dec 10  2016 config_datasource.properties
2913703 drwxr-xr-x   8 rocky  staff   256 Nov 21  2016 document
13822742 drwxr-xr-x   4 rocky  staff   128 Mar 21  2017 idea
13964509 -rw-r--r--   1 rocky  staff  8720 Dec  5  2016 npm-debug.log
674806 drwxr-xr-x  20 rocky  staff   640 Apr 28 20:45 project
670756 drwxr-xr-x  23 rocky  staff   736 Apr 24 23:55 software
13175726 drwxr-xr-x   5 rocky  staff   160 Sep  1  2018 workspace
rockydeMacBook-Pro:Work rocky$ find . -inum 4321587203 -delete
rockydeMacBook-Pro:Work rocky$ ls -li
total 32
13583397 -rwxrwxrwx@  1 rocky  staff   138 Dec 10  2016 config_datasource.properties
2913703 drwxr-xr-x   8 rocky  staff   256 Nov 21  2016 document
13822742 drwxr-xr-x   4 rocky  staff   128 Mar 21  2017 idea
13964509 -rw-r--r--   1 rocky  staff  8720 Dec  5  2016 npm-debug.log
674806 drwxr-xr-x  20 rocky  staff   640 Apr 28 20:45 project
670756 drwxr-xr-x  23 rocky  staff   736 Apr 24 23:55 software
13175726 drwxr-xr-x   5 rocky  staff   160 Sep  1  2018 workspace
rockydeMacBook-Pro:Work rocky$ 
~~~

一些Unix风格的文件系统(如ReiserFS)省略了inode表，但必须存储等效的数据以提供等效的功能。该数据可以称为stat数据，参考向程序提供数据的stat系统调用。

文件名和目录含义：

- inode不包含其hardlink名称，只包含其他文件元数据。
- Unix目录是关联结构的列表，每个结构包含一个文件名和一个inode编号。
- 文件系统驱动程序必须搜索目录，查找特定的文件名，然后将文件名转换为相应的inode编号。

## hardlink

要了解hardlink是什么，重要的是要了解文件的标识是它的inode号，而不是它的名称。hardlink是一个指向inode的名称。这意味着如果file1有一个名为file2的hardlink，那么这两个文件都引用相同的inode。因此，当您为一个文件创建一个hardlink时，您真正要做的就是为一个inode添加一个新的名称。为此，请使用不带选项的ln命令。

~~~verilog
# ls -l /home/bobbin/sync.sh  
-rw-r----- 1 root root 5 Apr 7 06:09 /home/bobbin/sync.sh
~~~

~~~verilog
# ln /home/bobbin/sync.sh synchro
~~~

现在让我们比较两个文件：

~~~verilog
# ls -il /home/bobbin/sync.sh synchro 
517333 -rw-r----- 2 root root 5 Apr 7 06:09 /home/bobbin/sync.sh
517333 -rw-r----- 2 root root 5 Apr 7 06:09 synchro
~~~

关于hard links的有趣之处在于原始文件和link之间没有差异：它们只是连接到同一inode的两个名称。

## **inode包含的属性**

- File types ( executable, block special etc )
- Permissions ( read, write etc )
- UID ( Owner )
- GID ( Group )
- FileSize
- Time stamps including last access, last modification and last inode number change.
- File deletion time
- Number of links ( soft/hard )
- Location of file on harddisk
- Some other metadata about file.

## inode随复制、移动和删除而更改

当复制、移动或删除文件系统上的文件时，inode编号会怎样。

复制文件：CP分配一个空闲的inode编号，并在inode表中放置一个新条目。

~~~verilog
### Check inode of existing file 
$ ls -il  myfile.txt
1150561 -rw-r--r-- 1 root root 0 Mar 10 01:06 myfile.txt

### Copy file with new name 
$ cp myfile.txt myfile_new.txt

### Check inode number of new file. Its changed 
$ ls -il myfile_new.txt
1150562 -rw-r--r-- 1 root root 0 Mar 10 01:09 myfile_new.txt
~~~

移动或重命名文件：如果目标与源文件系统相同，对inode编号没有影响，它只更改inode表中的时间戳。

~~~verilog
### Check inode of existing file 
$ ls -il  myfile.txt
1150561 -rw-r--r-- 1 root root 0 Mar 10 01:06 myfile.txt

### Moved file to another directory 
$ mv myfile.txt /opt/

### Check inode number of moved file. No change in inode 
$ ls -il /opt/myfile.txt
1150561 -rw-r--r-- 1 root root 0 Mar 10 01:06 /opt/myfile.txt
~~~

删除一个文件：在Linux中删除一个文件，减少链接计数，释放的inode编号会被重用。

**总结：**文件通过文件名进行访问，但事实上，对于文件本身并不与文件名称直接相关联。相反，文件通过inode来访问，inode使用唯一的数值进行标志。该值称为inode编号（inode number），通常简写为i-number或者ino。一个inode存储文件关联的元数据，如它的修改时间戳、所有者、类型、长度以及文件的数据的地址—唯独没有文件名。inode既是Unix文件系统在磁盘上实际物理对象，也是Linux内核中的数据结构的概念实体。

# file descriptor

在Unix和相关的计算机操作系统中，文件描述符(FD)是用于访问文件或其他输入/输出资源(如管道或者网络套接字)的抽象指示符(句柄)。文件描述符构成POSIX应用程序编程接口的一部分。文件描述符是一个非负整数，通常在C编程语言中表示为int类型(保留负值以表示“无值”或错误条件)。

在Unix的传统实现中，文件描述符被索引进由内核维护的进程内文件描述符表（每进程拥有），然后索引为所有进程共享的表示已打开文件的表，称为文件表。此表记录打开文件(或其他资源)的模式：用于读取、写入、附加以及可能的其他模式。它还索引到第三个表，称为inode表，该表描述实际的底层文件。**为了执行输入或输出，进程通过系统调用将文件描述符传递给内核，内核将代表进程访问文件。进程无法直接访问文件或inode表**。

在Linux上，进程中打开的一组文件描述符可以在路径/proc/pid/fd/下访问，其中PID是进程标识符。

在类似Unix的系统中，文件描述符可以引用在文件系统中命名的任何Unix文件类型。除了常规文件之外，它还包括目录、Blockand字符设备(也称为“特殊文件”)、Unix域套接字和命名管道。文件描述符还可以引用文件系统中通常不存在的其他对象，例如匿名管道和网络套接字。

![](../images/文件描述符-简单.jpg)

*示例-1：*

~~~c
#include <stdio.h>
#include <fcntl.h>

int main()
{
    char c;
    int fd = open("d:\\1.txt", O_RDONLY, 0);
    read(fd, &c, 1);

    printf("c = %c\n", c);
    exit(0);
}
~~~

输出：

~~~c
c = 1
~~~

上面程序中fd就是打开文件的文件描述符，read方法执行系统调用将文件描述符作为参数传入进去。由内核执行后续的操作。

*示例-2*

~~~c
#include <stdio.h>
#include <fcntl.h>

int main()
{
    int num;
    FILE *fptr;
    fptr = fopen("d:\\1.txt", "w");

    if(fptr == NULL) {
        printf("Error!");
        exit(1);
    }

    fprintf(fptr, "%d", 1213);
    fclose(fptr);

    return 0;
}
~~~

示例-2中我们可以看到没有看到文件描述符相关的信息，而是通过FILE类型的指针来进行操作。文件描述符是一个低级别的"句柄"，用于标识内核级、Linux和其他类Unix系统中打开的文件(或套接字或其他什么)。C语言对文件描述符进行了包装，提出了文件指针的概念。

文件指针是C标准库级结构，用于表示文件。FILE包装了文件描述符，并添加缓冲和其他功能，以使I/O更容易。

## **标准文件描述符**

在类似Unix的操作系统上，默认情况下，前三个文件描述符是STDIN(标准输入)、STDOUT(标准输出)和STDERR(标准错误)。

| 名称            | 数字 | 描述                                                     | 缩写   |
| --------------- | ---- | -------------------------------------------------------- | ------ |
| Standard input  | 0    | 标准输入流文件描述符。在终端中，默认为来自用户的键盘输入 | stdin  |
| Standard output | 1    | 标准输出流描述符。在终端中，默认为用户的屏幕             | stdout |
| Standard error  | 2    | 标准错误流描述符。在终端中，默认为用户的屏幕             | stderr |

# FileDescriptor类

上面示例-2中我们可以看到C语言提出了FILE这样的数据结构来包装文件描述符，使得我们能在一个较高的层次上进行文件的操作，而不用直接操作与内核有关的文件描述符。同样Java也进行了包装，提出了FileDescriptor类。

*实例-1*

~~~java
@Test
public void test_1() throws IOException {
    FileOutputStream fileOutputStream1 = new FileOutputStream(FileDescriptor.out);
    fileOutputStream1.write(65);
}
~~~

运行上面的方法，可以看到屏幕中输出A。

~~~
public static final FileDescriptor out = standardStream(1);
~~~

FileDescriptor类中定义了上面我们说到的三个标准文件描述符，这里我们使用的就是标准输出文件描述符。持有这个文件描述符那么我们就持有了操作终端屏幕的能力。上面的代码中我们没有使用常用的System.out.println方法来执行向终端屏幕输出字符的功能，而是直接使用标准输出文件描述符来操作，实现相同的效果。

*实例-2*

~~~java
@Test
public void test_2() throws IOException {
    File file = new File("D:/1.txt");
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    FileDescriptor fileDescriptor = fileOutputStream.getFD();
    FileOutputStream fileOutputStream1 = new FileOutputStream(fileDescriptor);
    fileOutputStream1.write(65);
}
~~~

程序中打开的文件与文件描述符关联。上面的代码中创建了两个FileOutputStream对象，但是它们使用的是一个文件描述符，所以使用第二个FileOutputStream可以实现对1.txt文件的写操作。