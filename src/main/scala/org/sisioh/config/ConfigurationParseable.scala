package org.sisioh.config

import com.typesafe.config.ConfigParseable

object ConfigurationParseable {

  def apply(configParseable: ConfigParseable): ConfigurationParseable =
    ConfigurationParseableImpl(configParseable)

}

trait ConfigurationParseable {

  val underlying: ConfigParseable

  def parse(options: ConfigurationParseOptions): ConfigurationObject

  def origin: ConfigurationOrigin

  def options: ConfigurationParseOptions

}

private[config]
case class ConfigurationParseableImpl
(underlying: ConfigParseable)
  extends ConfigurationParseable {

  def parse(options: ConfigurationParseOptions): ConfigurationObject =
    ConfigurationObject(underlying.parse(options.underlying))

  def origin: ConfigurationOrigin =
    ConfigurationOrigin(underlying.origin)

  def options: ConfigurationParseOptions = ConfigurationParseOptions(underlying.options)

}
