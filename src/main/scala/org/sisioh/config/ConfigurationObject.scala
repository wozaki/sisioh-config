package org.sisioh.config

import com.typesafe.config.ConfigObject
import scala.collection.JavaConverters._

object ConfigurationObject {

  def apply(core: ConfigObject): ConfigurationObject =
    new ConfigurationObjectImpl(core)

}


trait ConfigurationObject extends ConfigurationValue {

  protected[config] val core: ConfigObject

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
case class ConfigurationObjectImpl(core: ConfigObject)
  extends ConfigurationObject {

  def toConfig = Configuration(core.toConfig)

  def value: Option[Map[String, Any]] = Option(core.unwrapped()).map(_.asScala.toMap)

  def withFallback(other: ConfigurationMergeable): ConfigurationObject =
    ConfigurationObject(core.withFallback(other.core))

  def get(key: Any): Option[ConfigurationValue] = Option(core.get(key)).map(ConfigurationValue(_))

  def apply(key: Any): ConfigurationValue = get(key).get

  def withOnlyKey(key: String): ConfigurationObject = ConfigurationObject(core.withOnlyKey(key))

  def withoutKey(key: String): ConfigurationObject = ConfigurationObject(core.withoutKey(key))

  def withValue(key: String, value: ConfigurationValue): ConfigurationObject =
    ConfigurationObject(core.withValue(key, value.core))

  def origin: ConfigurationOrigin = ConfigurationOrigin(core.origin())

  def valueType  = ConfigurationValueType(core.valueType())

  def valueAsString: Option[String] = None

  def valueAsBoolean: Option[Boolean] = None

  def valueAsNumber: Option[Number] = None

  def valueAsSeq: Option[Seq[Any]] = None

  def valueAsMap: Option[Map[String, Any]] = value

  def atPath(path: String): Configuration = Configuration(core.atPath(path))

  def atKey(key: String): Configuration = Configuration(core.atKey(key))

}
