~~~
spring.jackson.date-format
~~~

That property is only used for the `java.util.Date` serialization and not `java.time.*` classes.

You can have on demand (per field) format specified with `@JsonFormat(pattern="dd.MM.yyyy")`

or

Create a bean that implements the `org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer` interface and setup custom serializers on the ObjectMapper for `java.time.*` classes that use a more sensible format.