import org.squeryl._
import org.squeryl.dsl._

object SquerylEntrypoint extends PrimitiveTypeMode {

  class PlanetPK(val value: Long) extends AnyVal {
    override def toString = s"PlanetPK($value)"
  }

  class MoonPK(val value: Long) extends AnyVal {
    override def toString = s"MoonPK($value)"
  }

  implicit val planetPKTEF = new NonPrimitiveJdbcMapper[Long, PlanetPK, TLong](longTEF, this) {
    def convertFromJdbc(v: Long) = new PlanetPK(v)
    def convertToJdbc(v: PlanetPK) = v.value
  }

  implicit def planetPKToTE(v: PlanetPK) = planetPKTEF.create(v)

  // copy & paste

  implicit val moonPKTEF = new NonPrimitiveJdbcMapper[Long, MoonPK, TLong](longTEF, this) {
    def convertFromJdbc(v: Long) = new MoonPK(v)
    def convertToJdbc(v: MoonPK) = v.value
  }

  implicit def moonPKToTE(v: MoonPK) = moonPKTEF.create(v)
}

import SquerylEntrypoint._

object SolarSystem extends Schema {

  case class Planet(id: PlanetPK, name: String)
  case class Moon(id: MoonPK, name: String, planetId: PlanetPK)

  val planets = table[Planet]("planet")
  val moons   = table[Moon]("moon")
}

object SquerylAnyValMain extends App {

  import SolarSystem._

  DB.init()

  inTransaction {
    //Session.currentSession.setLogger( println )

    println(
      from(planets)(p => where(p.id === new PlanetPK(1)) select(p)).headOption
    )

    def findPlanetById(id: PlanetPK) =
      from(planets)(p => where(p.id === id) select(p)).headOption

    // Won't compile: type mismatch
    //findPlanetById(new MoonPK(1))


    // Argh.  This compiles and runs, which is annoying.
    println(
      from(planets)(p => where(p.id === new MoonPK(1)) select(p)).headOption
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