package controllers

import play.api.mvc.{Controller, Action}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import models.Member
import models.CustomData

object ApiController extends Controller {
  def exist(category: String, name: String) = Action {
    val x = category match {
      case "uname" => Right(Member.selectByUname(_))
      case "email" => Right(Member.selectByEmail(_))
      case _ => Left("no such request point.")
    }
    x.right.map { f =>
      f(name) match {
        case Some(member) => (Json.toJson(Map("exist" -> 1)))
        case None => (Json.toJson(Map("exist" -> 0)))
      }
    } match {
      case Right(json) => Ok(json)
      case Left(message) => BadRequest(Json.toJson(Map("error" -> message)))
    }
  }

  val cssJsForm = Form(tuple(
      "token" -> text,
      "purpose" -> text,
      "contentType" -> text,
      "content"  -> text
      ))
  def cssOrJsPost = Action {
    implicit request => {
      val (token, purpose, contentType, content) = cssJsForm.bindFromRequest.get
      val uname = request.session.get("uname").getOrElse("")
      val memberId = Member.selectByUname(uname).get.id

      try {
        if (token != (request.session.get("token").getOrElse(""))) throw new Exception("CSRFトークンが一致しません。")

        contentType match {
          case "css" => CustomData.saveCss(memberId.toLong, purpose, content)
          case "js"  => CustomData.saveJs(memberId.toLong, purpose, content)
          case _     => throw new Exception("contentType が不正です。")
        }

        Ok(Json.toJson(Map("success" -> Json.toJson(1))))
      } catch {
        case e: Exception =>
          BadRequest(Json.toJson(Map("success" -> Json.toJson(0), "message" -> Json.toJson("エラー: " + e))))
      }
    }
  }

  def css(purpose: String, name: String) = Action { getCssOrJs(purpose, name, "css") }
  def js(purpose: String, name: String) = Action { getCssOrJs(purpose, name, "js") }

  def jsPost = TODO

  private def getCssOrJs(purpose: String, name: String, contentType: String) = {
    // TODO: ここで例外を吐くよりはJSONでエラーを返す方がベター
    if (purpose != "list" && purpose != "page")     throw new Exception("purpose は list か page のみサポートしています。")
    if (contentType != "css" && contentType!= "js") throw new Exception("contentType は css か jsのみサポートしています。")

    val memberId = Member.selectByUname(name).get.id
    Ok(Json.toJson(
        Map(
            "success" -> Json.toJson(1),
            "code" -> Json.toJson(
                if (contentType == "css") CustomData.loadCss(memberId, purpose) else CustomData.loadJs(memberId, purpose)
            )
        )))
  }
}
