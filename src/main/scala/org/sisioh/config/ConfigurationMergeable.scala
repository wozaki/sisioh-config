package org.sisioh.config

import com.typesafe.config.ConfigMergeable

trait ConfigurationMergeable {

  protected[config] val core: ConfigMergeable

  def withFallback(other: ConfigurationMergeable): ConfigurationMergeable

}
