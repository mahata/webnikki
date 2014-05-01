package controllers

import play.api.Play.current
import play.api.mvc.{Controller, Action}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.Play

import library.Util
import views._
import models.Member
import models.Post

object PostController extends Controller {
  val postForm = Form(
      tuple(
          "token" -> text,
          "title" -> text,
          "content" -> text,
          "created_at" -> text
          ))

  def index = Action {
    implicit request =>
      Ok(html.post(
        request.session.get("uname"),
        postForm.bind(Map(
          "token" -> request.session.get("token").getOrElse("")
        ))))
  }

  def indexEdit(createdAt: String) = Action {
    implicit request => {
      try {
        val member = Member.selectByUname(request.session.get("uname").getOrElse(""))
        val post = Post.postByMemberIdAndCreatedAt(member.get.id, createdAt.toLong).get
        Ok(html.post(
          request.session.get("uname"),
          postForm.bind(Map(
            "token" -> request.session.get("token").getOrElse(""),
            "title" -> post.title,
            "content" -> post.content,
            "created_at" -> post.created_at.toString
          ))))
      } catch {
        case e: Exception => BadRequest("編集する記事が存在しません。")
      }
    }
  }

  def indexPost = Action {
    implicit request => {
      try {
        val (token, title, content, created_at) = postForm.bindFromRequest.get
        if (token != (request.session.get("token").getOrElse(""))) throw new Exception("CSRFトークンが一致しません。")
        val member = Member.selectByUname(request.session.get("uname").getOrElse(""))
        val _created_at = if (created_at.isEmpty) {
          Post.create(member.get.id, title, content)
        } else {
          Post.update(member.get.id, title, content, created_at.toLong)
        }

        Ok(Json.toJson(Map(
            "success" -> Json.toJson(1),
            "created_at" -> Json.toJson(_created_at),
            "url" -> Json.toJson(createEntryUrl(request.session.get("uname").getOrElse(""), _created_at))
            )))
      } catch {
        case e: Exception => BadRequest(Json.toJson(Map(
            "success" -> Json.toJson(0),
            "message" -> Json.toJson("予期しないエラー " + e))
            ))
      }
    }
  }

  def deletePost = Action {
    implicit request => {
      try {
        val (token, _, _, created_at) = postForm.bindFromRequest.get
        if (token != (request.session.get("token").getOrElse(""))) throw new Exception("CSRFトークンが一致しません。")

        val member = Member.selectByUname(request.session.get("uname").getOrElse(""))
        Post.delete(member.get.id, created_at.toLong)

        Ok(Json.toJson(Map(
          "success" -> Json.toJson(1)
        )))
      } catch {
        case e: Exception => BadRequest(Json.toJson(Map(
          "success" -> Json.toJson(0),
          "message" -> Json.toJson("予期しないエラー " + e)
        )))
      }
    }
  }

  def imagePost = Action(parse.multipartFormData) {
    implicit request => {
      request.body.file("file").map { picture =>
        import java.io.File
        val filename = picture.filename 
        val contentType = picture.contentType

        val copyDir = Play.application.path + "/public/images/users/" + request.session.get("uname").get
        val copyPath = Util.calcUniquifiedFilePath(copyDir + "/" + filename)
        (new File(copyDir)).mkdirs
        picture.ref.moveTo(new File(copyPath))

        Ok(Json.toJson(Map(
          "success" -> Json.toJson(1),
          "path" -> Json.toJson(Util.urlifyFilePath(copyPath))
        )))
      }.getOrElse {
        BadRequest(Json.toJson(Map(
          "success" -> Json.toJson(0)
        )))
      }
    }
  }

  private def createEntryUrl(uname: String, createdAt: Long) =
    current.configuration.getString("service.url") + uname + "/" + createdAt.toString
}
