package controllers

// import play.api._
import play.api.mvc.{Controller, Action}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import library.Util
import views._

import models.Member
import models.Post
import models.Comment
import models.CustomData

import java.util.Date

object EntryController extends Controller {
  val commentForm = Form(
      tuple(
          "post_id" -> number,
          "uname" -> text,
          "content" -> text
          ))

//  def index(createdAt: String) = Action {
//    implicit request => {
//      val memberId = Member.selectByUname(Util.getUnameFromSubdomain(request.domain)).get.id
//      Post.postByMemberIdAndCreatedAt(memberId, createdAt.toLong) match {
//        case Some(entry) => {
//          val css = CustomData.loadCss(memberId, "page")
//          val js = CustomData.loadJs(memberId, "page")
//          val uname = Util.getUnameFromSubdomain(request.domain)
//          Ok(html.entry(request.session.get("uname"), css, js, uname, entry, Comment.commentsByPostId(entry.id)))
//        }
//        case None => BadRequest("その記事存在しないです...")
//      }
//    }
//  }

  def index(createdAt: String, uname: String) = Action {
    implicit request => {
      val memberId = Member.selectByUname(uname).get.id
      Post.postByMemberIdAndCreatedAt(memberId, createdAt.toLong) match {
        case Some(entry) => {
          val css = CustomData.loadCss(memberId, "page")
          val js = CustomData.loadJs(memberId, "page")
          val uname = Util.getUnameFromSubdomain(request.domain)
          Ok(html.entry(request.session.get("uname"), css, js, uname, entry, Comment.commentsByPostId(entry.id)))
        }
        case None => BadRequest("その記事存在しないです...")
      }
    }
  }

  def indexPost = Action {
    implicit request => {
      commentForm.bindFromRequest.value map { case (post_id, uname, content) =>
        try {
          val created_at = Comment.create(post_id, 0, uname, content, true)
          Ok(Json.toJson(Map(
              "success" -> Json.toJson(1),
              "created_at" -> Json.toJson(created_at),
              "formatted_created_at" -> Json.toJson("%tY/%<tm/%<td %<tH:%<tM:%<tS" format new Date(created_at))
              )))
        } catch {
          case e: Exception => BadRequest(Json.toJson(Map(
              "success" -> Json.toJson(0),
              "message" -> Json.toJson("エラー: " + e)
              )))
        }
      } getOrElse {
        BadRequest(Json.toJson(Map("success" -> Json.toJson(0), "message" -> Json.toJson("commentForm に関するエラー"))))
      }
    }
  }
}
