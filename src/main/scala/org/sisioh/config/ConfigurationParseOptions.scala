package org.sisioh.config

import com.typesafe.config.ConfigParseOptions

object ConfigurationParseOptions {

  def apply(): ConfigurationParseOptions = ConfigurationParseOptionsImpl()

  def apply(configParseOptions: ConfigParseOptions): ConfigurationParseOptions =
    ConfigurationParseOptionsImpl(configParseOptions)

  def defaults: ConfigurationParseOptions = apply(ConfigParseOptions.defaults())

}

trait ConfigurationParseOptions {

  val underlying: ConfigParseOptions

  def setSyntax(syntax: ConfigurationSyntax.Value): ConfigurationParseOptions

  def getSyntax: ConfigurationSyntax.Value

  def setOriginDescription(originDescription: String): ConfigurationParseOptions

  def getOriginDescription: String

  def setAllowMissing(allowMissing: Boolean): ConfigurationParseOptions

  def getAllowMissing: Boolean

  def setIncluder(includer: ConfigurationIncluder): ConfigurationParseOptions

  def prependIncluder(includer: ConfigurationIncluder): ConfigurationParseOptions

  def appendIncluder(includer: ConfigurationIncluder): ConfigurationParseOptions

  def getIncluder: ConfigurationIncluder

  def setClassLoader(loader: ClassLoader): ConfigurationParseOptions

  def getClassLoader: ClassLoader
}

private[config]
case class ConfigurationParseOptionsImpl
(underlying: ConfigParseOptions = ConfigParseOptions.defaults())
  extends ConfigurationParseOptions {

  def setSyntax(syntax: ConfigurationSyntax.Value): ConfigurationParseOptions =
    ConfigurationParseOptions(underlying.setSyntax(ConfigurationSyntax.toCore(syntax)))

  def getSyntax: ConfigurationSyntax.Value = ConfigurationSyntax(underlying.getSyntax)

  def setOriginDescription(originDescription: String): ConfigurationParseOptions =
    ConfigurationParseOptions(underlying.setOriginDescription(originDescription))

  def getOriginDescription: String = underlying.getOriginDescription

  def setAllowMissing(allowMissing: Boolean): ConfigurationParseOptions =
    ConfigurationParseOptions(underlying.setAllowMissing(allowMissing))

  def getAllowMissing: Boolean = underlying.getAllowMissing

  def setIncluder(includer: ConfigurationIncluder): ConfigurationParseOptions =
    ConfigurationParseOptions(underlying.setIncluder(includer.underlying))

  def prependIncluder(includer: ConfigurationIncluder): ConfigurationParseOptions =
    ConfigurationParseOptions(underlying.prependIncluder(includer.underlying))

  def appendIncluder(includer: ConfigurationIncluder): ConfigurationParseOptions =
    ConfigurationParseOptions(underlying.appendIncluder(includer.underlying))

  def getIncluder: ConfigurationIncluder = ConfigurationIncluder(underlying.getIncluder)

  def setClassLoader(loader: ClassLoader): ConfigurationParseOptions =
    ConfigurationParseOptions(underlying.setClassLoader(loader))

  def getClassLoader: ClassLoader = underlying.getClassLoader

}
