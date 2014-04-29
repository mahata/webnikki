package controllers

import play.api.mvc.{Controller, Action}
import models.Member
import models.Post
import models.CustomData
import views.html

object IndexController extends Controller {
  def index = Action {
    implicit request => {
      Ok(html.index(request.session.get("uname")))
    }
  }

  def user(uname: String) = Action {
    implicit request => {
      val memberId = Member.selectByUname(uname).get.id
      val css = CustomData.loadCss(memberId, "list")
      val js = CustomData.loadJs(memberId, "list")
      println("Yo")
      Ok(html.myindex(
        request.session.get("uname"),
        css,
        js,
        uname,
        Post.postsByMemberId(memberId, Some(0)),
        1,
        Map(
          "prev" -> false,
          "next" -> Post.hasNextPage(memberId, 1)))
      )
    }
  }

  def robots = Action {
    val robotsTxt =
      "User-agent: *\n" +
      "Disallow: /login\n" +
      "Disallow: /signup\n" +
      "Disallow: /manage\n" +
      "Disallow: /rss\n"
    Ok(robotsTxt)
  }

  def test = Action {
    // val appConf = Configuration.load()
    Ok("Hello")
    // Ok(html.test())
  }
}
