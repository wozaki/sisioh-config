package org.sisioh.config

import org.specs2.mutable.Specification
import java.io.File

class ConfigurationSpec extends Specification {

  "configuration in Map" should {

    val configInMap = Configuration.from(Map("foo.bar1" -> "value1", "foo.bar2" -> "value2", "blah" -> "value3"))

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
  }

  "configuration in File" should {

    val configInFile = Configuration.load(new File("src/test/resources"))

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

  }

}
