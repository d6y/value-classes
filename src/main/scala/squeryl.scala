import org.squeryl._
import org.squeryl.dsl._

object SquerylEntrypoint extends PrimitiveTypeMode {
}

import SquerylEntrypoint._

object SolarSystem extends Schema {

  case class Planet(id: Long, name: String)
  case class Moon(id: Long, name: String, planetId: Long)

  val planets = table[Planet]("planet")
  val moons   = table[Moon]("moon")
}

object SquerylAnyValMain extends App {

  import SolarSystem._

  DB.init()

  inTransaction {
    //Session.currentSession.setLogger( println )

    println(
      from(planets)(p => where(p.id === 1) select(p)).headOption
    )
  }
}


object DB {

  def init() : Unit = {
    import org.squeryl.SessionFactory
    import org.squeryl.Session
    import org.squeryl.adapters.PostgreSqlAdapter

    Class.forName("org.postgresql.Driver")
    SessionFactory.concreteFactory = Some(()=>
      Session.create(
          java.sql.DriverManager.getConnection("jdbc:postgresql:core-slick", "core", "trustno1"),
          new PostgreSqlAdapter))

  }
}