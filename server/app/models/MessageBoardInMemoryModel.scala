package models

import collection.mutable

object MessageBoardInMemoryModel {
    private val users = mutable.Map[String, String]("web" -> "apps", "mlewis" -> "prof")
    private val publicMessages = mutable.ListBuffer[(String, String)]("web" -> "Wow this app is neat")
    private val privateMessages = mutable.Map[String, mutable.ListBuffer[(String,String, String)]]("web" -> mutable.ListBuffer(("web", "mlewis", "This app is so good, I'm gonna give it 20 bonus points")), "mlewis" -> mutable.ListBuffer(("web", "mlewis", "This app is so good, I'm gonna give it 20 bonus points")))

    def validateUser(username: String, password: String): Boolean = {
        users.get(username).map(_==password).getOrElse(false)
    }

    def createUser(username: String, password: String): Boolean = {
        if(users.contains(username)) false else {
            users(username) = password
            true
        }
    }

    def userExists(username: String): Boolean = users.contains(username)

    def getPublicMessages(): mutable.ListBuffer[(String,String)] = publicMessages

    def getDMs(username: String): mutable.ListBuffer[(String,String,String)] = {
        privateMessages.get(username).getOrElse(mutable.ListBuffer())
    }

    def addPM(username: String, msg: String): Unit = {
        val x = (username, msg)
        publicMessages += x
    }

    def addDM(to: String, from: String, msg: String): Unit = {
        val x = (to, from, msg)
        if(to != from) {
            privateMessages(from) = privateMessages.get(from).getOrElse(mutable.ListBuffer()) :+ x
        }
        privateMessages(to) = privateMessages.get(to).getOrElse(mutable.ListBuffer()) :+ x
    }

}