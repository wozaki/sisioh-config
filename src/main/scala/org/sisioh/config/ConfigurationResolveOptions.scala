package org.sisioh.config

import com.typesafe.config.ConfigResolveOptions

object ConfigurationResolveOptions {

  private[config] def apply(useSystemEnvironment: Boolean): ConfigurationResolveOptions =
    new ConfigurationResolveOptionsImpl(useSystemEnvironment)

  def defaults: ConfigurationResolveOptions = apply(useSystemEnvironment = true)

  def noSystem: ConfigurationResolveOptions = apply(useSystemEnvironment = false)

}

trait ConfigurationResolveOptions {

  val underlying: ConfigResolveOptions

  def setUseSystemEnvironment(value: Boolean): ConfigurationResolveOptions

  def getUseSystemEnvironment: Boolean

}

private[config]
case class ConfigurationResolveOptionsImpl
(private val useSystemEnvironment: Boolean)
  extends ConfigurationResolveOptions {

  val underlying = ConfigResolveOptions.defaults.setUseSystemEnvironment(useSystemEnvironment)

  def setUseSystemEnvironment(value: Boolean): ConfigurationResolveOptions =
    ConfigurationResolveOptions(value)

  def getUseSystemEnvironment: Boolean = useSystemEnvironment

}

