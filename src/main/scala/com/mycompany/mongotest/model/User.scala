package com.mycompany.mongotest {
  package model {


    import _root_.net.liftweb.record._
    import _root_.net.liftweb.record.field._
    import _root_.net.liftweb.mongodb._
    import _root_.net.liftweb.mongodb.record._
    import _root_.net.liftweb.mongodb.record.field._
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._

    /**
     * The singleton that has methods for accessing the database
     */
    object User extends User with MongoMetaRecord[User] with MetaMegaProtoUser[User] {
      override def screenWrap = Full(<lift:surround with="default" at="content">
          <lift:bind /></lift:surround>)
      // define the order fields will appear in forms and output
      override def fieldOrder = List(id, firstName, lastName, email,
                                     locale, timezone, password)

      // comment this line out to require email validations
      override def skipEmailValidation = true
             
    }

    /**
     * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
     */
    class User private() extends MongoRecord[User] with MegaProtoUser[User] {
      def meta = User // what's the "meta" server
      protected def userFromStringId(id: String): Box[User] = meta.find(id)
      
      protected def findUserByUniqueId(id: String): Box[User] =  {
        var searchListHeadOption = meta.findAll("_id",id).headOption
        searchListHeadOption match {
          case Some(x) => Full(x)
          case None => return Empty
        }
      }
      /**
       * Given an username (probably email address), find the user
       */
      protected def findUserByEmail(email: String): Box[User] = {
        var searchListHeadOption = meta.findAll("email",email).headOption
        searchListHeadOption match {
          case Some(x) => Full(x)
          case None => return Empty
        }
      }
   

      protected def findUserByUserName(email: String): Box[User] = findUserByEmail(email)

      override def valUnique(errorMsg: => String)(emailValue: String) = {
        meta.findAll("email",emailValue) match {
          case Nil => Nil
          case usr :: Nil if (usr.id == id) => Nil
          case _ => List(FieldError(email, "The email should be unique"))
        }
      }

      // define an additional field for a personal essay
    }

  }
}
