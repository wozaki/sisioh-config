package org.sisioh.config

import com.typesafe.config.{ConfigValueType, ConfigValue}
import scala.collection.JavaConverters._

object ConfigurationValue {

  def apply(core: ConfigValue): ConfigurationValue =
    new ConfigurationValueImpl(core)

}


trait ConfigurationValue
  extends ConfigurationMergeable {

  val underlying: ConfigValue

  def origin: ConfigurationOrigin

  def valueType: ConfigurationValueType.Value

  def value: Option[Any]

  def valueAsString: Option[String]

  def valueAsBoolean: Option[Boolean]

  def valueAsNumber: Option[Number]

  def valueAsSeq: Option[Seq[Any]]

  def valueAsMap: Option[Map[String, Any]]

  def atPath(path: String): Configuration

  def atKey(key: String): Configuration

  def withFallback(other: ConfigurationMergeable): ConfigurationValue

}

private[config]
case class ConfigurationValueImpl(underlying: ConfigValue)
  extends ConfigurationValue {

  def origin = ConfigurationOrigin(underlying.origin())

  def valueType = ConfigurationValueType(underlying.valueType())

  def value: Option[Any] = {
    underlying.unwrapped() match {
      case null => None
      case v: java.util.Map[_, _] => Some(v.asScala)
      case v: java.util.List[_] => Some(v.asScala.toSeq)
      case v => Some(v)
    }
  }

  def valueAsString: Option[String] = {
    valueType match {
      case ConfigurationValueType.String =>
        value.map(_.asInstanceOf[String])
      case _ =>
        None
    }
  }

  def valueAsBoolean: Option[Boolean] = {
    valueType match {
      case ConfigurationValueType.Boolean =>
        value.map(_.asInstanceOf[Boolean])
      case _ =>
        None
    }
  }

  def valueAsNumber: Option[Number] = {
    valueType match {
      case ConfigurationValueType.Number =>
        value.map(_.asInstanceOf[Number])
      case _ =>
        None
    }
  }

  def valueAsSeq: Option[Seq[Any]] = {
    valueType match {
      case ConfigurationValueType.List =>
        value.map(_.asInstanceOf[Seq[_]])
      case _ =>
        None
    }
  }

  def valueAsMap: Option[Map[String, Any]] = {
    valueType match {
      case ConfigurationValueType.Object =>
        value.map(_.asInstanceOf[Map[String, _]])
      case _ =>
        None
    }
  }

  def withFallback(other: ConfigurationMergeable) =
    ConfigurationValue(underlying.withFallback(other.underlying))

  def atPath(path: String) = Configuration(underlying.atPath(path))

  def atKey(key: String) = Configuration(underlying.atKey(key))

}
