package org.sisioh.config

import com.typesafe.config.ConfigOrigin
import java.net.URL
import scala.collection.JavaConverters._


object ConfigurationOrigin {

  def apply(configOrigin: ConfigOrigin): ConfigurationOrigin =
    ConfigurationOriginImpl(configOrigin)

}


trait ConfigurationOrigin {

  val underlying: ConfigOrigin

  def description: Option[String]

  def filename: Option[String]

  def url: Option[URL]

  def resource: Option[String]

  def lineNumber: Int

  def comments: Seq[String]

}


private[config]
case class ConfigurationOriginImpl
(underlying: ConfigOrigin)
  extends ConfigurationOrigin {

  def description = Option(underlying.description())

  def filename = Option(underlying.filename())

  def url: Option[URL] = Option(underlying.url())

  def resource: Option[String] = Option(underlying.resource())

  def lineNumber: Int = underlying.lineNumber()

  def comments: Seq[String] = underlying.comments.asScala.toSeq

}
