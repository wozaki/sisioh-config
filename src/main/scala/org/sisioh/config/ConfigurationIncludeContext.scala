package org.sisioh.config

import com.typesafe.config.ConfigIncludeContext

object ConfigurationIncludeContext {

  def apply(configIncludeContext: ConfigIncludeContext): ConfigurationIncludeContext =
    ConfigurationIncludeContextImpl(configIncludeContext)

}


trait ConfigurationIncludeContext {

  protected[config] val core: ConfigIncludeContext

  def relativeTo(filename: String): ConfigurationParseable

  def parseOptions: ConfigurationParseOptions

}

private[config]
case class ConfigurationIncludeContextImpl
(protected[config] val core: ConfigIncludeContext)
  extends ConfigurationIncludeContext {

  def relativeTo(filename: String): ConfigurationParseable =
    ConfigurationParseable(core.relativeTo(filename))

  def parseOptions: ConfigurationParseOptions =
    ConfigurationParseOptions(core.parseOptions())

}
