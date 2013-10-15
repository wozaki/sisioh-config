package org.sisioh.config

import com.typesafe.config.ConfigIncludeContext

object ConfigurationIncludeContext {

  def apply(configIncludeContext: ConfigIncludeContext): ConfigurationIncludeContext =
    ConfigurationIncludeContextImpl(configIncludeContext)

}


trait ConfigurationIncludeContext {

  val underlying: ConfigIncludeContext

  def relativeTo(filename: String): ConfigurationParseable

  def parseOptions: ConfigurationParseOptions

}

private[config]
case class ConfigurationIncludeContextImpl
(val underlying: ConfigIncludeContext)
  extends ConfigurationIncludeContext {

  def relativeTo(filename: String): ConfigurationParseable =
    ConfigurationParseable(underlying.relativeTo(filename))

  def parseOptions: ConfigurationParseOptions =
    ConfigurationParseOptions(underlying.parseOptions())

}
