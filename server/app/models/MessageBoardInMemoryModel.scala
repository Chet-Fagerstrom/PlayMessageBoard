package models

import collection.mutable

object MessageBoardInMemoryModel {
    private val users = mutable.Map[String, String]("web" -> "apps", "mlewis" -> "prof")
    private val publicMessages = mutable.ListBuffer[(String, String)]("web" -> "Wow this app is neat")
    private val privateMessages = mutable.Map[String, mutable.ListBuffer[(String,String, String)]]("web" -> mutable.ListBuffer(("web", "mlewis", "This app is so good, I'm gonna give it 20 bonus points")), "mlewis" -> mutable.ListBuffer(("web", "mlewis", "This app is so good, I'm gonna give it 20 bonus points")))

    def validateUser(username: String, password: String): Boolean = synchronized {
        users.get(username).map(_==password).getOrElse(false)
    }

    def createUser(username: String, password: String): Boolean = synchronized {
        if(users.contains(username)) false else {
            users(username) = password
            true
        }
    }

    def userExists(username: String): Boolean = synchronized {users.contains(username)}

    def getPublicMessages(): Seq[(String, String)] = synchronized { publicMessages.toSeq }

    def getDMs(username: String): Seq[(String,String,String)] = synchronized {
            privateMessages.get(username).getOrElse(Seq()).toSeq
    }

    def addPM(username: String, msg: String): Unit = synchronized {
        val x = (username, msg)
        publicMessages += x
    }

    def addDM(to: String, from: String, msg: String): Unit = synchronized {
        val x = (to, from, msg)
        if(to != from) {
            privateMessages(from) = privateMessages.get(from).getOrElse(mutable.ListBuffer()) :+ x
        }
        privateMessages(to) = privateMessages.get(to).getOrElse(mutable.ListBuffer()) :+ x

    }

}