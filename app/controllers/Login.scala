package controllers

import play.api._
import play.api.mvc._

object Login extends Controller {
  def index = Action {
    Ok(views.html.login("Hello, this is login!"))
  }
}
