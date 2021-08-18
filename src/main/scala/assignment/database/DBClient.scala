package assignment.database

import assignment.Webhook
import com.typesafe.scalalogging.LazyLogging
import scalikejdbc.config.DBs
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

object DBClient extends Storage with LazyLogging {
  DBs.setupAll()

  def putWebhook(wh: Webhook): Unit = {
    DB.localTx { implicit session =>
      sql"""insert into webhooks(url, event, id) values (${wh.url}, ${wh.event}, ${wh.id})"""
        .update()
        .apply()
    }
  }

  def deleteWebhook(id: String): Unit = {
    DB.localTx { implicit session =>
      sql"""delete from webhooks values where id = $id"""
        .update()
        .apply()
    }
  }

  def getWebhook(id: String): Option[Webhook] = {
    DB.readOnly { implicit session =>
      sql"""select * from webhooks where id = $id"""
        .map(x => Webhook(x.string("url"), x.string("event"), x.string("id")))
        .list()
        .apply()
        .headOption
    }
  }

  def modifyWebhook(id: String, maybeUrl: Option[String], maybeEvent: Option[String]): Unit = {
    DB.localTx { implicit session =>

      val maybeQuery = (maybeUrl, maybeEvent) match {
        case (Some(url), Some(event)) =>
          Some(sql"""update webhooks set url = $url, event = $event where id = $id""")
        case (Some(url), None) =>
          Some(sql"""update webhooks set url = $url where id = $id""")
        case (None, Some(event)) =>
          Some(sql"""update webhooks set event = $event where id = $id""")
        case _ =>
          logger.error(s"Invalid webhook update request with id $id, both arguments are empty")
          None
      }

      maybeQuery.foreach(_.update().apply())
    }
  }
}
