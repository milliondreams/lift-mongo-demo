package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.com.mycompany.mongotest.model._
import net.liftweb.mongodb.{MongoDB, DefaultMongoIdentifier,MongoAddress, MongoHost}


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    //Connect to the DB - local on Localhost which should have a collection "user"
    MongoDB.defineDb(
      DefaultMongoIdentifier,
      MongoAddress(MongoHost("localhost", 27017), "local"))

    // where to search snippet
    LiftRules.addToPackages("com.mycompany.mongotest")

    // Build SiteMap
    def sitemap() = SiteMap(
      Menu("Home") / "index" >> User.AddUserMenusAfter, // Simple menu form
      // Menu with special Link
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

    LiftRules.setSiteMapFunc(() => User.sitemapMutator(sitemap()))

    /*
     * Show the spinny image when an Ajax call starts
     */
     LiftRules.ajaxStart =
       Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

     /*
      * Make the spinny image go away when it ends
      */
     LiftRules.ajaxEnd =
       Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

     LiftRules.early.append(makeUtf8)

     LiftRules.loggedInTest = Full(() => User.loggedIn_?)

     }

     /**
      * Force the request to be UTF-8
      */
     private def makeUtf8(req: HTTPRequest) {
        req.setCharacterEncoding("UTF-8")
      }
     }
