package org.sisioh.config

import com.typesafe.config.ConfigOrigin
import scala.collection.JavaConverters._
import java.net.URL

case class ConfigurationOrigin(configOrigin: ConfigOrigin) {

  def description() = Option(configOrigin.description())

  def filename() = Option(configOrigin.filename())

  def url(): Option[URL] = Option(configOrigin.url())

  def resource(): Option[String] = Option(configOrigin.resource())

  def lineNumber: Int = configOrigin.lineNumber()

  def comments: Seq[String] = configOrigin.comments.asScala.toSeq

}
