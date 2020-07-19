IOC方式，将bean的创建交于第三方容器管理，Spring负责bean的实例化。本章详细讨论Spring实例化bean的几种方式。

# 一、创建对象的方式

创建对象有以下几种方式。

## 1.1. 使用关键字new

这是创建对象的最常见和常规的方法，也是一种非常简单的方法。通过使用此方法，我们可以调用要调用的构造函数（无参数的构造函数和参数化的）。

~~~java
Obj obj = new Obj();
~~~

## 1.2. 使用反射创建对象

使用Java的反射机制，在知晓对象的Class对象的情况下可以通过反射机制来创建对象。

~~~java
#Employee emp2 = (Employee) Class.forName("org.programming.mitra.exercises.Employee").newInstance();

#Constructor<Employee> constructor = Employee.class.getConstructor();
#Employee emp3 = constructor.newInstance();

public class NewInstanceExample 
{ 
    String name = "GeeksForGeeks"; 
    public static void main(String[] args) 
    { 
        try
        { 
            Class cls = Class.forName("NewInstanceExample"); 
            NewInstanceExample obj = 
                    (NewInstanceExample) cls.newInstance(); 
            System.out.println(obj.name); 
        } 
        catch (ClassNotFoundException e) 
        { 
            e.printStackTrace(); 
        } 
        catch (InstantiationException e) 
        { 
            e.printStackTrace(); 
        } 
        catch (IllegalAccessException e) 
        { 
            e.printStackTrace(); 
        } 
    } 
} 
~~~

## 1.3. 使用clone()方法

每当我们在任何对象上调用clone()时，JVM实际上都会为我们创建一个新对象，并将先前对象的所有内容复制到其中。使用clone方法创建对象不会调用任何构造函数。

~~~java
public class CloneExample implements Cloneable 
{ 
    @Override
    protected Object clone() throws CloneNotSupportedException 
    { 
        return super.clone(); 
    } 
    String name = "GeeksForGeeks"; 
  
    public static void main(String[] args) 
    { 
        CloneExample obj1 = new CloneExample(); 
        try
        { 
            CloneExample obj2 = (CloneExample) obj1.clone(); 
            System.out.println(obj2.name); 
        } 
        catch (CloneNotSupportedException e) 
        { 
            e.printStackTrace(); 
        } 
    } 
} 
~~~

java.lang.Object方法是一个本地方法，这也说明了对象的克隆处理是JVM来实现的。

~~~java
protected native Object clone() throws CloneNotSupportedException;
~~~

## 1.4. 使用反序列化

每当我们序列化和反序列化一个对象时，JVM都会为我们创建一个单独的对象。在反序列化中，JVM不使用任何构造函数来创建对象。

~~~java
public class DeserializationExample implements Serializable 
{ 
    private String name; 
    DeserializationExample(String name) 
    { 
        this.name = name; 
    } 
  
    public static void main(String[] args) 
    { 
        try
        { 
            DeserializationExample d = 
                    new DeserializationExample("GeeksForGeeks"); 
            FileOutputStream f = new FileOutputStream("file.txt"); 
            ObjectOutputStream oos = new ObjectOutputStream(f); 
            oos.writeObject(d); 
            oos.close(); 
            f.close(); 
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        } 
    } 
} 

public class DeserializationExample 
{ 
    public static void main(String[] args) 
    { 
        try
        { 
            DeserializationExample d; 
            FileInputStream f = new FileInputStream("file.txt"); 
            ObjectInputStream oos = new ObjectInputStream(f); 
            d = (DeserializationExample)oos.readObject(); 
        } 
        catch (Exception e) 
        { 
            e.printStackTrace(); 
        } 
        System.out.println(d.name); 
    } 
} 
~~~

# 二、Spring实例化Bean

创建对象的方式上面描述了，使用Spring，对象的创建交给了Spring容器，对于主动创建对象来说，这种方式就是一种非主动的方式，通过书写相关的配置后，Spring会根据用户的bean配置信息来创建对象，这是一种间接创建对象的方式。并且，Spring还提供了几种用于实例化bean的方式。只要按照Spring定义的方法规则书写即可。





