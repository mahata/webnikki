package library

import play.Play
import java.io.File

object Util {
  // this function is obsolete and will be removed later (because it's migrated from webnikki.jp)
  def getUnameFromSubdomain(subdomain: String) = subdomain.split('.')(0)

  def getUnameFromPath(path: String) =
    path.indexOf("/", 1) match {
      case -1 if path.length == 0 => ""
      case -1 => path.substring(1)
      case _ => path.substring(1, path.indexOf("/", 1))
    }

  def calcUniquifiedFilePath(originalPath: String) = {
    def makeNewFileCandidate(dirName: String, fileBaseName: String, fileExtName: String, prefixNum: Integer): String = {
      val temporaryPath = s"$dirName/$fileBaseName-$prefixNum.$fileExtName"

      (new File(temporaryPath)).exists match {
        case true => makeNewFileCandidate(dirName, fileBaseName, fileExtName, prefixNum + 1)
        case false => temporaryPath
      }
    }

    (new File(originalPath)).exists match {
      case true => {
        val dirName = originalPath.substring(0, originalPath.lastIndexOf('/'))
        val fileName = originalPath.substring(originalPath.lastIndexOf('/') + 1, originalPath.length)
        val fileBaseName = fileName.substring(0, fileName.lastIndexOf('.'))
        val fileExtName = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length)

        makeNewFileCandidate(dirName, fileBaseName, fileExtName, 1)
      }
      case false => originalPath
    }
  }

  def urlifyFilePath(originalPath: String) =
    originalPath.substring(Play.application.path.toString.length, originalPath.length).replace("/public/", "/")

  def base64encode(str: String) =
    new sun.misc.BASE64Encoder().encode(str.getBytes).toString

  def base64decode(encodedStr: String): String =
    new sun.misc.BASE64Decoder().decodeBuffer(encodedStr).map(b => b.toChar).mkString

  def sha1(input: String): String = {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    md.digest(input.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }
}
