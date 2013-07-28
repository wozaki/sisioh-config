package org.sisioh.config

case class ConfigurationException(message: String, cause: Throwable, origin: ConfigurationOrigin)
  extends Exception(message, cause)

