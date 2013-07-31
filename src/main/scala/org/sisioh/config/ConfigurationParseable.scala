package org.sisioh.config

import com.typesafe.config.ConfigParseable

object ConfigurationParseable {

  def apply(configParseable: ConfigParseable): ConfigurationParseable =
    ConfigurationParseableImpl(configParseable)

}

trait ConfigurationParseable {

  protected[config] val core: ConfigParseable

  def parse(options: ConfigurationParseOptions): ConfigurationObject

  def origin: ConfigurationOrigin

  def options: ConfigurationParseOptions

}

private[config]
case class ConfigurationParseableImpl
(protected[config] val core: ConfigParseable)
  extends ConfigurationParseable {

  def parse(options: ConfigurationParseOptions): ConfigurationObject =
    ConfigurationObject(core.parse(options.core))

  def origin: ConfigurationOrigin =
    ConfigurationOrigin(core.origin)

  def options: ConfigurationParseOptions = ConfigurationParseOptions(core.options)

}
