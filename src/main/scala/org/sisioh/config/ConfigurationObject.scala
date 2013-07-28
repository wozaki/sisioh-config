package org.sisioh.config

import com.typesafe.config.{ConfigMergeable, ConfigObject}
import scala.collection.JavaConverters._

case class ConfigurationObject(configObject: ConfigObject) {

  def toConfig = Configuration(configObject.toConfig)

  def unwrapped: Map[String, Any] = configObject.unwrapped().asScala.toMap

  def withFallback(other: ConfigMergeable): ConfigurationObject =
    ConfigurationObject(configObject.withFallback(other))

  def get(key: Any): ConfigurationValue = ConfigurationValue(configObject.get(key))

  def withOnlyKey(key: String): ConfigurationObject = ConfigurationObject(configObject.withOnlyKey(key))

  def withoutKey(key: String): ConfigurationObject = ConfigurationObject(configObject.withoutKey(key))

  def withValue(key: String, value: ConfigurationValue): ConfigurationObject =
    ConfigurationObject(configObject.withValue(key, value.configValue))

}
