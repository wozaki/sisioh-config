package org.sisioh.config

import org.specs2.mutable.Specification
import java.io.File
import com.typesafe.config.ConfigObject

class ConfigurationSpec extends Specification {

  "configuration in Map" should {

    val configInMap = Configuration.parseMap(Map("foo.bar1" -> "value1", "foo.bar2" -> "value2", "blah" -> "value3"))

    "be accessible as an entry set" in {
      val map = Map(configInMap.entrySet.toList: _*)
      map.keySet must contain("foo.bar1", "foo.bar2", "blah").only
    }

    "make all paths accessible" in {
      configInMap.keys must contain("foo.bar1", "foo.bar2", "blah").only
    }

    "make all sub keys accessible" in {
      configInMap.subKeys must contain("foo", "blah").only
    }

    "get value as boolean" in {
      val config = Configuration.parseMap(Map("boolean.v1" -> false))
      config.getBooleanValue("boolean.v1") must beSome(false)
    }

    "get values as boolean" in {
      val config = Configuration.parseMap(Map("boolean.v2" -> Seq(false, true)))
      config.getBooleanValues("boolean.v2") must beSome(Seq(false, true))
    }

  }

  "configuration in File" should {

    val configInFile = Configuration.parseFile(new File("src/test/resources/conf/application.conf"))

    "be accessible as an entry set" in {
      val map = Map(configInFile.entrySet.toList: _*)
      map.keySet must contain("foo.bar1", "foo.bar2", "blah").only
    }

    "make all paths accessible" in {
      configInFile.keys must contain("foo.bar1", "foo.bar2", "blah").only
    }

    "make all sub keys accessible" in {
      configInFile.subKeys must contain("foo", "blah").only
    }

    "get value as boolean" in {
      val Some(configValue) = configInFile.getConfigurationValue("boolean.v1")
      configValue.valueAsBoolean must beSome(false)
      configInFile.getBooleanValue("boolean.v1") must beSome(false)
    }

    "get values as boolean" in {
      configInFile.getBooleanValues("boolean.v2") must beSome(Seq(false, true))
    }

    "get configuration" in {
      val Some(config) = configInFile.getConfiguration("foo")
      config.getStringValue("bar1") must beSome("value1")
    }

    "get value as config object" in {
      val configObjectOpt = configInFile.getConfigurationObject("object.v1")
      configObjectOpt must beSome
      val Some(configObject) = configObjectOpt
      configObject("id").valueAsString must beSome("a")
      configObject("name").valueAsString must beSome("b")
      val objectv2 = configInFile.getConfigurationObject("object.v2")

      configObject.withFallback(objectv2.get)("age").valueAsNumber must beSome(20)
    }

  }

}
