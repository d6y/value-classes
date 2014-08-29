object SlickAnyVal extends App {

  // code gen
  class PlanetPK(val value: Long) extends AnyVal {
    override def toString = s"PlanetPK($value)"
  }

  class MoonPK(val value: Long) extends AnyVal {
    override def toString = s"MoonPK($value)"
  }

  trait Tables {
    val profile: scala.slick.driver.JdbcProfile
    import profile.simple._

    // Code gen
    implicit val planetPKMapper = MappedColumnType.base[PlanetPK, Long](_.value, new PlanetPK(_))
    implicit val moonPKMapper   = MappedColumnType.base[MoonPK,   Long](_.value, new MoonPK(_))

    case class Planet(name: String, id: PlanetPK=new PlanetPK(0))

    class PlanetTable(tag: Tag) extends Table[Planet](tag, "planet") {
      def id   = column[PlanetPK]("id", O.PrimaryKey, O.AutoInc)
      def name = column[String]("name")
      def * = (name, id) <> (Planet.tupled, Planet.unapply)
    }

    lazy val planets = TableQuery[PlanetTable]


    // Moons orbit one planet
    case class Moon(name: String, planetId: PlanetPK, id: MoonPK=new MoonPK(0))

    class MoonTable(tag: Tag) extends Table[Moon](tag, "moon") {
      def id       = column[MoonPK]("id", O.PrimaryKey, O.AutoInc)
      def planetId = column[PlanetPK]("planet_id")
      def name     = column[String]("name")

      def * = (name, planetId, id) <> (Moon.tupled, Moon.unapply)

      def planet = foreignKey("planet_fk", planetId, planets)(_.id)
    }

    lazy val moons = TableQuery[MoonTable]
  }


  import scala.slick.driver.PostgresDriver.simple._

  object PgTables extends {
    val profile = scala.slick.driver.PostgresDriver
  } with Tables {
    val db = Database.forURL("jdbc:postgresql:core-slick", user="core", password="trustno1", driver = "org.postgresql.Driver")
  }

  import PgTables._

  db.withSession { implicit s =>
    //(planets.ddl ++ moons.ddl).drop

    (planets.ddl ++ moons.ddl).create
    val earth = planets returning planets += Planet("Earth")
    val moon = moons returning moons += Moon("The Moon", earth.id)
    println(s"Inserted: $earth and $moon")

    // OK:
    val earthId = new PlanetPK(1L)
    println(planets.filter(_.id === earthId).list)

    // No: "Cannot perform option-mapped operation"
    //println(planets.filter(_.id === 1L).list)
    //println(planets.filter(_.id === new MoonPK(1)).list)

    // OK:
    def findPlanet(id: PlanetPK) = planets.filter(_.id === id).list
    findPlanet(earth.id)

  }

}



/*
Without AnyVal

classes (master #)$ javap 'SlickAnyVal$Tables$Moon.class'
Compiled from "slick.scala"
public class SlickAnyVal$Tables$Moon implements scala.Product,scala.Serializable {
  public final SlickAnyVal$Tables $outer;
  public java.lang.String name();
  public SlickAnyVal$PlanetPK planetId();
  public SlickAnyVal$MoonPK id();


  With AnyVal:
classes (master #)$ javap 'SlickAnyVal$Tables$Moon.class'
Compiled from "slick.scala"
public class SlickAnyVal$Tables$Moon implements scala.Product,scala.Serializable {
  public final SlickAnyVal$Tables $outer;
  public java.lang.String name();
  public long planetId();
  public long id();


Methods are boiled away to long too:

public SlickAnyVal$$anonfun$6$$anonfun$findPlanet$1$1(SlickAnyVal$$anonfun$6, long);



*/