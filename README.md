sisioh-config
=============

sisioh-config is scala wrapper for typesafe config.

## Configuration File Example
```
foo.bar1 = value1
foo.bar2 = value2

db = { driverClassName: com.mysql.jdbc.Driver, url: jdbc:mysql://localhost/test }
```


## API Example
```scala
val config = Configuration.load(new File("conf"))
val Some(bar1) = config.getIntValue("foo.bar1") // value1

val Some(foo) = config.getConfiguration("foo")
val Some(bar2) = foo.getIntValue("bar2") // value2

val Some(dbConfigObject) = config.getConfigurationObject("db")
val Some(driverClassName) = dbConfigObject.get("driverClassName") // com.mysql.jdbc.Driver
val Some(url) = dbConfigObject.get("url") // jdbc:mysql://localhost/test
```

