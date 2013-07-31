package org.sisioh.config

import com.typesafe.config._
import java.io._
import scala.collection.JavaConverters._
import scala.util.{Failure, Try}


/**
 * This object provides a set of operations to create `Configuration` values.
 *
 * For example, to load a `Configuration` in a running application:
 * {{{
 * val config = Configuration.load()
 * val foo = config.getString("foo").getOrElse("boo")
 * }}}
 *
 * The underlying implementation is provided by https://github.com/typesafehub/config.
 */
object Configuration {

  private[this] lazy val dontAllowMissingConfigOptions = ConfigParseOptions.defaults().setAllowMissing(false)

  private[this] lazy val dontAllowMissingConfig = ConfigFactory.load(dontAllowMissingConfigOptions)

  /**
   * loads `Configuration` from config.resource or config.file. If not found default to 'conf/application.conf' in Dev mode
   * @return  configuration to be used
   */
  private[config] def loadDev(appkey: File, devSettings: Map[String, String]): Config = {
    try {
      lazy val file = {
        devSettings.get("config.file").orElse(Option(System.getProperty("config.file")))
          .map(f => new File(f)).getOrElse(new File(appkey, "conf/application.conf"))
      }
      val config = Option(System.getProperty("config.resource"))
        .map(ConfigFactory.parseResources(_)).getOrElse(ConfigFactory.parseFileAnySyntax(file))

      ConfigFactory.parseMap(devSettings.asJava).withFallback(ConfigFactory.load(config))
    } catch {
      case e: ConfigException => throw configError(ConfigurationOrigin(e.origin), e.getMessage, Some(e))
    }
  }

  /**
   * Loads a new `Configuration` either from the classkey or from
   * `conf/application.conf` depending on the application's Mode.
   *
   * The provided mode is used if the application is not ready
   * yet, just like when calling this method from `play.api.Application`.
   *
   * Defaults to Mode.Dev
   *
   * @param mode Application mode.
   * @return a `Configuration` instance
   */
  def load(appKey: File, mode: ConfigurationMode.Value = ConfigurationMode.Dev, devSettings: Map[String, String] = Map.empty) = {
    Try {
      if (mode == ConfigurationMode.Prod)
        Configuration(dontAllowMissingConfig)
      else
        Configuration(loadDev(appKey, devSettings))
    }.recoverWith {
      case e: ConfigException =>
        Failure(configError(ConfigurationOrigin(e.origin), e.getMessage, Some(e)))
    }.get
  }

  /**
   * Returns an empty Configuration object.
   */
  def empty = Configuration(ConfigFactory.empty())

  /**
   * Create a ConfigFactory object from the data passed as a Map.
   */
  def from(data: Map[String, Any]) = {
    val jMap = data.map {
      case (k, v: Map[_, _]) => (k, v.asJava)
      case (k, v: Iterable[_]) => (k, v.asJava)
      case (k, v) => (k, v)
    }.asJava
    Configuration(ConfigFactory.parseMap(jMap))
  }

  def configError(origin: ConfigurationOrigin, message: String, e: Option[Throwable] = None): Exception = {
    new ConfigurationException("configuration error", e.orNull, origin)
  }

  def apply(config: Config): Configuration =
    new ConfigurationImpl(config)


}

trait Configuration extends ConfigurationMergeable {

  val core: Config

  def root: ConfigurationObject

  def hasPath(path: String): Boolean

  def isEmpty: Boolean

  def resolve: Configuration

  def resolve(option: ConfigurationResolveOptions): Configuration

  def checkValid(reference: Configuration, restrictToPaths: String*): Try[Unit]

  def ++(other: Configuration): Configuration

  def getConfiguration(key: String): Option[Configuration]

  def getConfigurations(key: String): Option[Seq[Configuration]]

  def getConfigurationObject(key: String): Option[ConfigurationObject]

  def getConfigurationObjects(key: String): Option[Seq[ConfigurationObject]]

  def getConfigurationValue(key: String): Option[ConfigurationValue]

  def getConfigurationValues(key: String): Option[Seq[ConfigurationValue]]

  def getStringValue(key: String, validValues: Option[Set[String]] = None): Option[String]

  def getStringValues(key: String): Option[Seq[String]]

  def getNumberValue(key: String): Option[Number]

  def getNumberValues(key: String): Option[Seq[Number]]

  def getIntValue(key: String): Option[Int]

  def getIntValues(key: String): Option[Seq[Int]]

  def getLongValue(key: String): Option[Long]

  def getLongValues(key: String): Option[Seq[Long]]

  def getBooleanValue(key: String): Option[Boolean]

  def getBooleanValues(key: String): Option[Seq[Boolean]]

  def getMillisecondValue(key: String): Option[Long]

  def getMillisecondValues(key: String): Option[Seq[Long]]

  def getNanosecondValue(key: String): Option[Long]

  def getNanosecondValues(key: String): Option[Seq[Long]]

  def getByteValue(key: String): Option[Long]

  def getByteValues(key: String): Option[Seq[Long]]

  def getDoubleValue(key: String): Option[Double]

  def getDoubleValues(key: String): Option[Seq[Double]]

  def keys: Set[String]

  def subKeys: Set[String]

  def entrySet: Set[(String, ConfigValue)]

  def withOnlyPath(path: String): Configuration

  def withoutPath(path: String): Configuration

  def withValue(path: String, value: ConfigurationValue): Configuration

}

private[config]
case class ConfigurationImpl(core: Config) extends Configuration {

  def resolve(option: ConfigurationResolveOptions): Configuration =
    Configuration(core.resolve(option.core))

  def ++(other: Configuration): Configuration = {
    Configuration(other.core.withFallback(core))
  }

  private def readValue[T](key: String, v: => T): Option[T] = {
    Try {
      Option(v)
    }.recover {
      case e: ConfigException.Missing => None
    }.recoverWith {
      case e: Throwable =>
        Failure(reportError(key, e.getMessage, Some(e)))
    }.get
  }

  /**
   * Retrieves a configuration value as a `String`.
   *
   * This method supports an optional set of valid values:
   * {{{
   * val config = Configuration.load()
   * val mode = config.getString("engine.mode", Some(Set("dev","prod")))
   * }}}
   *
   * A configuration error will be thrown if the configuration value does not match any of the required values.
   *
   * @param validValues valid values for this configuration
   * @return a configuration value
   */
  def getStringValue(key: String, validValues: Option[Set[String]] = None): Option[String] =
    readValue(key, core.getString(key)).map {
      value =>
        validValues match {
          case Some(values) if values.contains(value) => value
          case Some(values) if values.isEmpty => value
          case Some(values) => throw reportError(key, "Incorrect value, one of " + (values.reduceLeft(_ + ", " + _)) + " was expected.")
          case None => value
        }
    }

  /**
   * Retrieves a configuration value as an `Int`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val poolSize = configuration.getInt("engine.pool.size")
   * }}}
   *
   * A configuration error will be thrown if the configuration value is not a valid `Int`.
   *
   * @param key the configuration key, relative to the configuration root key
   * @return a configuration value
   */
  def getIntValue(key: String): Option[Int] = readValue(key, core.getInt(key))

  /**
   * Retrieves a configuration value as a `Boolean`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val isEnabled = configuration.getString("engine.isEnabled")
   * }}}
   *
   * A configuration error will be thrown if the configuration value is not a valid `Boolean`.
   * Authorized vales are yes/no or true/false.
   *
   * @param key the configuration key, relative to the configuration root key
   * @return a configuration value
   */
  def getBooleanValue(key: String): Option[Boolean] = readValue(key, core.getBoolean(key))

  /**
   * Retrieves a configuration value as `Milliseconds`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val timeout = configuration.getMilliseconds("engine.timeout")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.timeout = 1 second
   * }}}
   */
  def getMillisecondValue(key: String): Option[Long] = readValue(key, core.getMilliseconds(key))

  /**
   * Retrieves a configuration value as `Nanoseconds`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val timeout = configuration.getNanoseconds("engine.timeout")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.timeout = 1 second
   * }}}
   */
  def getNanosecondValue(key: String): Option[Long] = readValue(key, core.getNanoseconds(key))

  /**
   * Retrieves a configuration value as `Bytes`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val maxSize = configuration.getString("engine.maxSize")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.maxSize = 512k
   * }}}
   */
  def getByteValue(key: String): Option[Long] = readValue(key, core.getBytes(key))

  /**
   * Retrieves a sub-configuration, i.e. a configuration instance containing all keys starting with a given prefix.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val engineConfig = configuration.getSub("engine")
   * }}}
   *
   * The root key of this new configuration will be ‘engine’, and you can access any sub-keys relatively.
   *
   * @param key the root prefix for this sub-configuration
   * @return a new configuration
   */
  def getConfiguration(key: String): Option[Configuration] = readValue(key, core.getConfig(key)).map(Configuration(_))

  /**
   * Retrieves a configuration value as a `Double`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val population = configuration.getDouble("world.population")
   * }}}
   *
   * A configuration error will be thrown if the configuration value is not a valid `Double`.
   *
   * @param key the configuration key, relative to the configuration root key
   * @return a configuration value
   */
  def getDoubleValue(key: String): Option[Double] = readValue(key, core.getDouble(key))

  /**
   * Retrieves a configuration value as a `Long`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val duration = configuration.getLong("timeout.duration")
   * }}}
   *
   * A configuration error will be thrown if the configuration value is not a valid `Long`.
   *
   * @param key the configuration key, relative to the configuration root key
   * @return a configuration value
   */
  def getLongValue(key: String): Option[Long] = readValue(key, core.getLong(key))

  /**
   * Retrieves a configuration value as a `Number`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val counter = configuration.getNumber("foo.counter")
   * }}}
   *
   * A configuration error will be thrown if the configuration value is not a valid `Number`.
   *
   * @param key the configuration key, relative to the configuration root key
   * @return a configuration value
   */
  def getNumberValue(key: String): Option[Number] = readValue(key, core.getNumber(key))

  /**
   * Retrieves a configuration value as a List of `Boolean`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val switches = configuration.getBooleanList("board.switches")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * board.switches = [true, true, false]
   * }}}
   *
   * A configuration error will be thrown if the configuration value is not a valid `Boolean`.
   * Authorized vales are yes/no or true/false.
   */
  def getBooleanValues(key: String): Option[Seq[Boolean]] = readValue[Seq[Boolean]](key, core.getBooleanList(key).asScala.toSeq.map(e => if (e) true else false))

  /**
   * Retrieves a configuration value as a List of `Bytes`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val maxSizes = configuration.getBytesList("engine.maxSizes")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.maxSizes = [512k, 256k, 256k]
   * }}}
   */
  def getByteValues(key: String): Option[Seq[Long]] =
    readValue(key, core.getBytesList(key).asScala.toSeq.map(e => e.toLong))

  /**
   * Retrieves a List of sub-configurations, i.e. a configuration instance for each key that matches the key.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val engineConfigs = configuration.getConfigList("engine")
   * }}}
   *
   * The root key of this new configuration will be "engine", and you can access any sub-keys relatively.
   */
  def getConfigurations(key: String): Option[Seq[Configuration]] =
    readValue(key, core.getConfigList(key)).map {
      configs => configs.asScala.map(Configuration(_))
    }

  /**
   * Retrieves a configuration value as a List of `Double`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val maxSizes = configuration.getDoubleList("engine.maxSizes")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.maxSizes = [5.0, 3.34, 2.6]
   * }}}
   */
  def getDoubleValues(key: String): Option[Seq[Double]] =
    readValue(key, core.getDoubleList(key).asScala.toSeq.map(_.toDouble))

  /**
   * Retrieves a configuration value as a List of `Integer`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val maxSizes = configuration.getIntList("engine.maxSizes")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.maxSizes = [100, 500, 2]
   * }}}
   */
  def getIntValues(key: String): Option[Seq[Int]] =
    readValue(key, core.getIntList(key).asScala.map(e => e.toInt).toSeq)

  def getConfigurationValue(key: String): Option[ConfigurationValue] =
    readValue(key, ConfigurationValue(core.getValue(key)))

  /**
   * Gets a list value (with any element type) as a ConfigList, which implements java.util.List<ConfigValue>.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val maxSizes = configuration.getList("engine.maxSizes")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.maxSizes = ["foo", "bar"]
   * }}}
   */
  def getConfigurationValues(key: String): Option[Seq[ConfigurationValue]] =
    readValue(key, core.getList(key).asScala.map(ConfigurationValue(_)).toSeq)

  /**
   * Retrieves a configuration value as a List of `Long`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val maxSizes = configuration.getLongList("engine.maxSizes")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.maxSizes = [10000000000000, 500, 2000]
   * }}}
   */
  def getLongValues(key: String): Option[Seq[Long]] =
    readValue(key, core.getLongList(key).asScala.toSeq.map(e => e.toLong))

  /**
   * Retrieves a configuration value as List of `Milliseconds`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val timeouts = configuration.getMillisecondsList("engine.timeouts")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.timeouts = [1 second, 1 second]
   * }}}
   */
  def getMillisecondValues(key: String): Option[Seq[Long]] =
    readValue(key, core.getMillisecondsList(key).asScala.toSeq.map(e => e.toLong))

  /**
   * Retrieves a configuration value as List of `Nanoseconds`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val timeouts = configuration.getNanosecondsList("engine.timeouts")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.timeouts = [1 second, 1 second]
   * }}}
   */
  def getNanosecondValues(key: String): Option[Seq[Long]] =
    readValue(key, core.getNanosecondsList(key).asScala.toSeq.map(e => e.toLong))

  /**
   * Retrieves a configuration value as a List of `Number`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val maxSizes = configuration.getNumberList("engine.maxSizes")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.maxSizes = [50, 500, 5000]
   * }}}
   */
  def getNumberValues(key: String): Option[Seq[Number]] =
    readValue(key, core.getNumberList(key).asScala.toSeq)

  /**
   * Retrieves a configuration value as a List of `ConfigObject`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val engineProperties = configuration.getObjectList("engine.properties")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.properties = [{id: 5, power: 3}, {id: 6, power: 20}]
   * }}}
   */
  def getConfigurationObjects(key: String): Option[Seq[ConfigurationObject]] =
    readValue[Seq[ConfigurationObject]](key, core.getObjectList(key).asScala.map(ConfigurationObject(_)).toSeq)

  /**
   * Retrieves a configuration value as a List of `String`.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val names = configuration.getStringList("names")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * names = ["Jim", "Bob", "Steve"]
   * }}}
   */
  def getStringValues(key: String): Option[Seq[String]] = readValue(key, core.getStringList(key).asScala.toSeq)

  /**
   * Retrieves a ConfigObject for this key, which implements Map<String,ConfigValue>
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val engineProperties = configuration.getObject("engine.properties")
   * }}}
   *
   * The configuration must be provided as:
   *
   * {{{
   * engine.properties = {id: 1, power: 5}
   * }}}
   */
  def getConfigurationObject(key: String): Option[ConfigurationObject] = readValue(key, ConfigurationObject(core.getObject(key)))

  /**
   * Returns available keys.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val keys = configuration.keys
   * }}}
   *
   * @return the set of keys available in this configuration
   */
  def keys: Set[String] = core.entrySet.asScala.map(_.getKey).toSet

  /**
   * Returns sub-keys.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * val subKeys = configuration.subKeys
   * }}}
   * @return the set of direct sub-keys available in this configuration
   */
  def subKeys: Set[String] = core.root().keySet().asScala.toSet

  /**
   * Returns every key as a set of key to value pairs, by recursively iterating through the
   * config objects.
   */
  def entrySet: Set[(String, ConfigValue)] = core.entrySet().asScala.map(e => e.getKey -> e.getValue).toSet

  /**
   * Creates a configuration error for a specific configuration key.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * throw configuration.reportError("engine.connectionUrl", "Cannot connect!")
   * }}}
   *
   * @param key the configuration key, related to this error
   * @param message the error message
   * @param e the related exception
   * @return a configuration exception
   */
  def reportError(key: String, message: String, e: Option[Throwable] = None): Exception = {
    val origin = if (core.hasPath(key)) core.getValue(key).origin else core.root.origin
    Configuration.configError(ConfigurationOrigin(origin), message, e)
  }

  /**
   * Creates a configuration error for this configuration.
   *
   * For example:
   * {{{
   * val configuration = Configuration.load()
   * throw configuration.globalError("Missing configuration key: [yop.url]")
   * }}}
   *
   * @param message the error message
   * @param e the related exception
   * @return a configuration exception
   */
  def globalError(message: String, e: Option[Throwable] = None): Exception = {
    Configuration.configError(ConfigurationOrigin(core.root.origin), message, e)
  }

  def withFallback(other: ConfigurationMergeable): ConfigurationMergeable =
    Configuration(core.withFallback(other.core))

  def root: ConfigurationObject = ConfigurationObject(core.root())

  def hasPath(path: String): Boolean = core.hasPath(path)

  def isEmpty: Boolean = core.isEmpty

  def withOnlyPath(path: String): Configuration = Configuration(core.withOnlyPath(path))

  def withoutPath(path: String): Configuration = Configuration(core.withoutPath(path))

  def withValue(path: String, value: ConfigurationValue): Configuration =
    Configuration(core.withValue(path, value.core))

  def resolve: Configuration = Configuration(core.resolve)

  def checkValid(reference: Configuration, restrictToPaths: String*): Try[Unit] = Try {
    core.checkValid(reference.core, restrictToPaths: _*)
  }
}
