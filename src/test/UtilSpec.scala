package test

import org.specs2.mutable._
import library.Util

class UtilSpec extends Specification {
  "Util.getUnameFromPath" should {
    "trim username from access path (i.e. /user)" in {
      Util.getUnameFromPath("/user") must equalTo("user")
      Util.getUnameFromPath("/user/") must equalTo("user")
      Util.getUnameFromPath("/user/dummy") must equalTo("user")
      Util.getUnameFromPath("//") must equalTo("")
      Util.getUnameFromPath("/") must equalTo("")
      Util.getUnameFromPath("") must equalTo("")
    }
  }

  "Util.base64decode" should {
    "convert \"c2VjcmV0UGEkJHdvcmQ=\" to \"secretPa$$word\"" in {
      Util.base64decode("c2VjcmV0UGEkJHdvcmQ=") must equalTo("secretPa$$word")
    }
  }

  "Util.sha1" should {
    "covert \"apple\" to \"d0be2dc421be4fcd0172e5afceea3970e2f3d940\"" in {
      Util.sha1("apple") must equalTo("d0be2dc421be4fcd0172e5afceea3970e2f3d940")
    }
  }
}