package org.sisioh.config

import com.typesafe.config.ConfigIncluder

object ConfigurationIncluder {

  def apply(configIncluder: ConfigIncluder): ConfigurationIncluder =
    ConfigurationIncluderImpl(configIncluder)

}

trait ConfigurationIncluder {

  val underlying: ConfigIncluder

  def withFallback(fallback: ConfigurationIncluder): ConfigurationIncluder

  def include(context: ConfigurationIncludeContext, what: String): ConfigurationObject

}

private[config]
case class ConfigurationIncluderImpl
(val underlying: ConfigIncluder)
  extends ConfigurationIncluder {

  def withFallback(fallback: ConfigurationIncluder): ConfigurationIncluder =
    ConfigurationIncluder(underlying.withFallback(fallback.underlying))

  def include(context: ConfigurationIncludeContext, what: String): ConfigurationObject =
    ConfigurationObject(underlying.include(context.underlying, what))

}
