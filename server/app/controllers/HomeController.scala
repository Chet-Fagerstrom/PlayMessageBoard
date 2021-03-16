package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.MessageBoardInMemoryModel

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def messageBoard = Action { implicit request =>
    val usernameOpt = request.session.get("username")
    usernameOpt.map { username => 
    val user = username
    val dms = MessageBoardInMemoryModel.getDMs(username)
    val msgs = MessageBoardInMemoryModel.getPublicMessages()
    Ok(views.html.messageboard(user, dms, msgs))
    }.getOrElse(Redirect(routes.HomeController.login()))
  }

  def login = Action { implicit request =>
    Ok(views.html.login()).withNewSession
  }

  def sendPM = Action { implicit request =>
    val usernameOpt = request.session.get("username")
    usernameOpt.map { username =>
    val postVals = request.body.asFormUrlEncoded
      postVals.map { args => 
        val msg = args("newPM").head
        MessageBoardInMemoryModel.addPM(username, msg)
        Redirect(routes.HomeController.messageBoard())
      }.getOrElse(Redirect(routes.HomeController.messageBoard()))
    }.getOrElse(Redirect(routes.HomeController.login()))

  }

  def sendDM = Action { implicit request =>
    val usernameOpt = request.session.get("username")
    usernameOpt.map { username =>
    val postVals = request.body.asFormUrlEncoded
      postVals.map { args => 
        val msg = args("newDM").head
        val to = args("DMRecipient").head
        if(MessageBoardInMemoryModel.userExists(to)){
        MessageBoardInMemoryModel.addDM(to, username, msg)
        Redirect(routes.HomeController.messageBoard())
        } else {
          Redirect(routes.HomeController.messageBoard()).flashing("error"->"Recipient does not exist")
        }
      }.getOrElse(Redirect(routes.HomeController.messageBoard()))
    }.getOrElse(Redirect(routes.HomeController.login()))
  }

  def createUser = Action { implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      if (MessageBoardInMemoryModel.createUser(username,password)){
        Redirect(routes.HomeController.messageBoard()).withSession("username" -> username)
      } else {
        Redirect(routes.HomeController.login()).flashing("error"->"Username is taken")
      }
    }.getOrElse(Redirect(routes.HomeController.login()))
  }

  def validateLogin = Action { implicit request =>
    val postVals = request.body.asFormUrlEncoded
    postVals.map { args =>
      val username = args("username").head
      val password = args("password").head
      if (MessageBoardInMemoryModel.validateUser(username,password)){
        Redirect(routes.HomeController.messageBoard()).withSession("username" -> username)
      } else {
        Redirect(routes.HomeController.login()).flashing("error"->"Invalid username/password combination")
      }
    }.getOrElse(Redirect(routes.HomeController.login()))
  }

  def logout = Action {
    Redirect(routes.HomeController.login()).withNewSession
  }
}
