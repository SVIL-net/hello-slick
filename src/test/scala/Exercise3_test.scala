import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta._

class TablesSuite extends funsuite.AnyFunSuite with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  
  val suppliers = TableQuery[Suppliers]
  val coffees = TableQuery[Coffees]

  var db: Database = _

  def createSchema() =
    db.run((suppliers.schema ++ coffees.schema).create).futureValue
      
  def insertSupplier() = {
    val suppliersInsert: DBIO[Unit] = DBIO.seq(
      suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
      suppliers += ( 49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
      suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")
    )
    db.run(suppliersInsert).futureValue
  }
  def insertCoffees() = {
    val coffeesInsert: DBIO[Option[Int]] = coffees ++= Seq (
        ("Colombian",         101, 7.99, 20, 0),
        ("French_Roast",       49, 8.99, 50, 0),
        ("Espresso",          150, 9.99, 10, 0),
        ("Colombian_Decaf",   101, 8.99, 40, 0),
        ("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
    db.run(coffeesInsert).futureValue
  }

  before { db = Database.forConfig("h2mem1") }

    test("stokingCheck works") {
    
    createSchema()
    insertSupplier()
    insertCoffees()
    val x = Exercise3.stokingCheck()
    Exercise3.printList()
  }

}