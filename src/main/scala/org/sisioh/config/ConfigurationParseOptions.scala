package org.sisioh.config

import com.typesafe.config.ConfigParseOptions

object ConfigurationParseOptions {

  def apply(): ConfigurationParseOptions = ConfigurationParseOptionsImpl()

  def apply(configParseOptions: ConfigParseOptions): ConfigurationParseOptions =
    ConfigurationParseOptionsImpl(configParseOptions)

}

trait ConfigurationParseOptions {

  protected[config] val core: ConfigParseOptions

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
(protected[config] val core: ConfigParseOptions = ConfigParseOptions.defaults())
  extends ConfigurationParseOptions {

  def setSyntax(syntax: ConfigurationSyntax.Value): ConfigurationParseOptions =
    ConfigurationParseOptions(core.setSyntax(ConfigurationSyntax.toCore(syntax)))

  def getSyntax: ConfigurationSyntax.Value = ConfigurationSyntax(core.getSyntax)

  def setOriginDescription(originDescription: String): ConfigurationParseOptions =
    ConfigurationParseOptions(core.setOriginDescription(originDescription))

  def getOriginDescription: String = core.getOriginDescription

  def setAllowMissing(allowMissing: Boolean): ConfigurationParseOptions =
    ConfigurationParseOptions(core.setAllowMissing(allowMissing))

  def getAllowMissing: Boolean = core.getAllowMissing

  def setIncluder(includer: ConfigurationIncluder): ConfigurationParseOptions =
    ConfigurationParseOptions(core.setIncluder(includer.core))

  def prependIncluder(includer: ConfigurationIncluder): ConfigurationParseOptions =
    ConfigurationParseOptions(core.prependIncluder(includer.core))

  def appendIncluder(includer: ConfigurationIncluder): ConfigurationParseOptions =
    ConfigurationParseOptions(core.appendIncluder(includer.core))

  def getIncluder: ConfigurationIncluder = ConfigurationIncluder(core.getIncluder)

  def setClassLoader(loader: ClassLoader): ConfigurationParseOptions =
    ConfigurationParseOptions(core.setClassLoader(loader))

  def getClassLoader: ClassLoader = core.getClassLoader

}
