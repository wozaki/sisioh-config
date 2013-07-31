package org.sisioh.config

import com.typesafe.config.ConfigSyntax

object ConfigurationSyntax extends Enumeration {

  val Json, Conf, Properties = Value

  protected[config] def apply(value: ConfigSyntax): ConfigurationSyntax.Value = {
    values.find {
      _.toString.toUpperCase == value.name().toUpperCase
    }.get
  }

  protected[config] def toCore(value: ConfigurationSyntax.Value): ConfigSyntax = {
    ConfigSyntax.values.find {
      _.name().toUpperCase == value.toString.toUpperCase
    }.get
  }

}
