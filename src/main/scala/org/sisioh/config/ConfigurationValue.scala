package org.sisioh.config

import com.typesafe.config.{ConfigMergeable, ConfigRenderOptions, ConfigValue}

case class ConfigurationValue(configValue: ConfigValue) {

  def origin = ConfigurationOrigin(configValue.origin())

  def valueType = configValue.valueType()

  def unwrapped = configValue.unwrapped()

  def render = configValue.render()

  def render(options: ConfigRenderOptions) = configValue.render(options)

  def withFallback(other: ConfigMergeable) = ConfigurationValue(configValue.withFallback(other))

  def atPath(path: String) = Configuration(configValue.atPath(path))

  def atKey(key: String) = Configuration(configValue.atKey(key))

}
