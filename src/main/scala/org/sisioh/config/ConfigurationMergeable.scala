package org.sisioh.config

import com.typesafe.config.ConfigMergeable

trait ConfigurationMergeable {

  val underlying: ConfigMergeable

  def withFallback(other: ConfigurationMergeable): ConfigurationMergeable

}
