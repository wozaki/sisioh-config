package org.sisioh.config

import com.typesafe.config.ConfigOrigin
import scala.collection.JavaConverters._
import java.net.URL

case class ConfigurationOrigin(configOrigin: ConfigOrigin) {

  def description() = configOrigin.description()

  def filename() = configOrigin.filename()

  def url(): URL = configOrigin.url()

  def resource(): String = configOrigin.resource()

  def lineNumber: Int = configOrigin.lineNumber()

  def comments: Seq[String] = configOrigin.comments.asScala.toSeq

}
