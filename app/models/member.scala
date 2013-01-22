package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import java.security.MessageDigest

import library.Random

case class Member(
  id: Long,
  uname: String,
  password: String,
  salt: String,
  email: String)

object Member {
  val member = {
    get[Long]("id") ~
      get[String]("uname") ~
      get[String]("password") ~
      get[String]("salt") ~
      get[String]("email") map {
        case id ~ uname ~ password ~ salt ~ email => Member(id, uname, password, salt, email)
      }
  }
  val saltLength = 64
  val stretchNum = 1000

  //  def all(): List[Member] = DB.withConnection {
  //    implicit c => SQL("SELECT * FROM member").as(member *)
  //  }

  private def selectBy(field: String, value: String) = {
    DB.withConnection {
      implicit c =>
        {
          val member = SQL("SELECT id, uname, password, salt, email FROM member WHERE %s = {value}" format field).on("value" -> value).apply()
          if (member.isEmpty) None else Some(member.head)
        }
    }
  }

  private def stretch(password: String, num: Int): String = {
    val md = MessageDigest.getInstance("SHA-512")

    num match {
      case 0 => password
      case _ => stretch(md.digest(password.getBytes).map(_ & 0xFF).map(_.toHexString).mkString, num - 1)
    }
  }

  def selectByUname(uname: String) =
    selectBy("uname", uname)

  def selectByEmail(email: String) =
    selectBy("email", email)

  def create(
    uname: String,
    email: String,
    password: String) {
    val salt = Random.randomString(saltLength)
    val stretchedPassword = stretch(password + salt, stretchNum)
    val sql =
      "INSERT INTO member (uname, email, password, salt) " +
      "VALUES ({uname}, {email}, {password}, {salt})"

    DB.withConnection {
      implicit c =>
        SQL(sql).on(
          "uname" -> uname,
          "email" -> email,
          "password" -> stretchedPassword,
          "salt" -> salt
          ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection {
      implicit c =>
        SQL("DELETE FROM member WHERE id = {id}").on(
          "id" -> id).executeUpdate()
    }
  }

  def isValidPassword(email: String, password: String): Boolean = {
    val member= selectBy("email", email)
    if (member.isEmpty) return false

    // return stretch(password + member.get.salt, stretchNum) == member.get.password
    return stretch(password + member.get[String]("salt"), stretchNum) == member.get[String]("password")
  }
}
