package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._
import models.Post

object PostController extends Controller {
  val postForm = Form(
      tuple(
          "token" -> text,
          "title" -> text,
          "content" -> text
          ))

  def index = Action {
    implicit request =>
      Ok(html.post(postForm.bind(Map("token" -> request.session.get("token").getOrElse(""))), None))
  }

//  def indexPost = Action {
//    implicit request =>
//      postForm.bindFromRequest.fold(
//          formWithErrors => {
//            BadRequest(html.post(formWithErrors, None))
//          }, {
//            case (token, title, content) => {
//              try {
//                Ok(html.postSuccess(""))
//              } catch {
//                case e: Exception => {
//                  BadRequest(
//                      html.post(
//                          postForm.bind(Map("token" -> token, "title" -> title, "content" -> content)),
//                          Some("ここで例外が投げられるのってどういうときなんだろう...ちょっと想像つかないな。")
//                          ))
//                }
//              }
//            }
//          })
//  }

  def indexPost = Action {
    implicit request => {
      val (token, title, content) = postForm.bindFromRequest().get
      try {
        if (token != (request.session.get("token").getOrElse(""))) throw new Exception("CSRFトークンが一致しません。")
        Post.create(title, content)
        Ok("書き込み成功！")
      } catch {
        case e: Exception => BadRequest("エラー: " + e)
      }
    }
  }
}