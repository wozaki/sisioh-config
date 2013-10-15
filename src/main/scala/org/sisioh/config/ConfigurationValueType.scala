package org.sisioh.config

import com.typesafe.config.ConfigValueType

object ConfigurationValueType extends Enumeration {

  val Object, List, Number, Boolean, Null, String = Value

  protected[config] def apply(value: ConfigValueType): ConfigurationValueType.Value = {
    values.find {
      _.toString.toUpperCase == value.name().toUpperCase
    }.get
  }

  protected[config] def toCore(value: ConfigurationValueType.Value): ConfigValueType = {
    ConfigValueType.values.find {
      _.name().toUpperCase == value.toString.toUpperCase
    }.get
  }

}
