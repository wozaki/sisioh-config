sisioh-config
=============

sisioh-config is scala wrapper for typesafe config.

## API Example
```scala
val config = Configuration.load(new File("conf"))
val Some(bar1) = config.getIntValue("foo.bar")

val Some(foo) = config.getConfiguration("foo")
val Some(bar2) = foo.getIntValue("bar")

val Some(dbConfigObject) = config.getConfigurationObject("db")
val Some(driverClassName) = dbConfigObject.get("driverClassName")
val Some(url) = dbConfigObject.get("url")
```

