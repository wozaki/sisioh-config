package org.sisioh.config

import com.typesafe.config.ConfigOrigin
import java.net.URL
import scala.collection.JavaConverters._


object ConfigurationOrigin {

  def apply(configOrigin: ConfigOrigin): ConfigurationOrigin =
    ConfigurationOriginImpl(configOrigin)

}


trait ConfigurationOrigin {

  protected[config] val core: ConfigOrigin

  def description: Option[String]

  def filename: Option[String]

  def url: Option[URL]

  def resource: Option[String]

  def lineNumber: Int

  def comments: Seq[String]

}


private[config]
case class ConfigurationOriginImpl
(protected[config] val core: ConfigOrigin)
  extends ConfigurationOrigin {

  def description = Option(core.description())

  def filename = Option(core.filename())

  def url: Option[URL] = Option(core.url())

  def resource: Option[String] = Option(core.resource())

  def lineNumber: Int = core.lineNumber()

  def comments: Seq[String] = core.comments.asScala.toSeq

}
