package org.sisioh.config

import com.typesafe.config.ConfigObject
import scala.collection.JavaConverters._

object ConfigurationObject {

  def apply(core: ConfigObject): ConfigurationObject =
    new ConfigurationObjectImpl(core)

}


trait ConfigurationObject extends ConfigurationValue {

  val underlying: ConfigObject

  def toConfig: Configuration

  def value: Option[Map[String, Any]]

  def get(key: Any): Option[ConfigurationValue]

  def apply(key: Any): ConfigurationValue

  def withOnlyKey(key: String): ConfigurationObject

  def withoutKey(key: String): ConfigurationObject

  def withValue(key: String, value: ConfigurationValue): ConfigurationObject

  def withFallback(other: ConfigurationMergeable): ConfigurationObject

}

private[config]
case class ConfigurationObjectImpl(underlying: ConfigObject)
  extends ConfigurationObject {

  def toConfig = Configuration(underlying.toConfig)

  def value: Option[Map[String, Any]] = Option(underlying.unwrapped()).map(_.asScala.toMap)

  def withFallback(other: ConfigurationMergeable): ConfigurationObject =
    ConfigurationObject(underlying.withFallback(other.underlying))

  def get(key: Any): Option[ConfigurationValue] = Option(underlying.get(key)).map(ConfigurationValue(_))

  def apply(key: Any): ConfigurationValue = get(key).get

  def withOnlyKey(key: String): ConfigurationObject = ConfigurationObject(underlying.withOnlyKey(key))

  def withoutKey(key: String): ConfigurationObject = ConfigurationObject(underlying.withoutKey(key))

  def withValue(key: String, value: ConfigurationValue): ConfigurationObject =
    ConfigurationObject(underlying.withValue(key, value.underlying))

  def origin: ConfigurationOrigin = ConfigurationOrigin(underlying.origin())

  def valueType  = ConfigurationValueType(underlying.valueType())

  def valueAsString: Option[String] = None

  def valueAsBoolean: Option[Boolean] = None

  def valueAsNumber: Option[Number] = None

  def valueAsSeq: Option[Seq[Any]] = None

  def valueAsMap: Option[Map[String, Any]] = value

  def atPath(path: String): Configuration = Configuration(underlying.atPath(path))

  def atKey(key: String): Configuration = Configuration(underlying.atKey(key))

}
