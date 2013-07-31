package org.sisioh.config

import com.typesafe.config.ConfigIncluder

object ConfigurationIncluder {

  def apply(configIncluder: ConfigIncluder): ConfigurationIncluder =
    ConfigurationIncluderImpl(configIncluder)

}

trait ConfigurationIncluder {

  protected[config] val core: ConfigIncluder

  def withFallback(fallback: ConfigurationIncluder): ConfigurationIncluder

  def include(context: ConfigurationIncludeContext, what: String): ConfigurationObject

}

private[config]
case class ConfigurationIncluderImpl
(protected[config] val core: ConfigIncluder)
  extends ConfigurationIncluder {

  def withFallback(fallback: ConfigurationIncluder): ConfigurationIncluder =
    ConfigurationIncluder(core.withFallback(fallback.core))

  def include(context: ConfigurationIncludeContext, what: String): ConfigurationObject =
    ConfigurationObject(core.include(context.core, what))

}
