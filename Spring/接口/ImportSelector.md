导入选择器接口。通过Import注解可以导入@Configuration类。而这个接口提供了根据条件导入@Configuration类的能力。

该接口只有一个方法，方法的返回值为通过选择后选中的@Configuration配置类的名称集。

~~~java
public interface ImportSelector {

	/**
	 * Select and return the names of which class(es) should be imported based on
	 * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
	 */
	String[] selectImports(AnnotationMetadata importingClassMetadata);

}
~~~

