import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta._
import scala.concurrent.ExecutionContext.Implicits.global

class TablesSuite extends funsuite.AnyFunSuite with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val suppliers = TableQuery[Suppliers]
  val coffees = TableQuery[Coffees]
  
  implicit var db: Database = _

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
        ("Colombian",         101, 7.99, 0, 0),
        ("French_Roast",       49, 8.99, 0, 0),
        ("Espresso",          150, 9.99, 10, 0),    //salesの値を10に変更
        ("Colombian_Decaf",   101, 8.99, 0, 0),
        ("French_Roast_Decaf", 49, 9.99, 10, 0)     //salesの値を10に変更
    )
    db.run(coffeesInsert).futureValue
  }

  before { db = Database.forConfig("h2mem1") }
 
 //Coffeesテーブル更新関数テスト
 test("Coffees table update works") {
    createSchema()
    insertSupplier()
    insertCoffees()
    Exercise.updateCoffeeTable("Colombian",20)
    val r1 = db.run(coffees.filter(_.name === "Colombian").map(_.sales).result).futureValue
    assert(r1.head == 20) 
    Exercise.updateCoffeeTable("Colombian",30)
    val r2 = db.run(coffees.filter(_.name === "Colombian").map(_.sales).result).futureValue
    assert(r2.head == 50)  
  }
  
  //補充対象仕入先・コーヒー名・個数表示関数テスト
  test("Replenishment works") {
    createSchema()
    insertSupplier()
    insertCoffees()
    val r = Exercise.replenishment()
    Exercise.printList()
    assert(r(0) == ("The High Ground", "Espresso", 10))
    assert(r(1) == ("Superior Coffee", "French_Roast_Decaf", 10))    
  }

  test("Creating the Schema works") {
    createSchema()
    
    val tables = db.run(MTable.getTables).futureValue

    assert(tables.size == 2)
    assert(tables.count(_.name.name.equalsIgnoreCase("suppliers")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("coffees")) == 1)
  }

  test("Inserting a Supplier works") {
    createSchema()
    insertSupplier()
    val insertCount = db.run(suppliers.result).futureValue
    assert(insertCount.size == 3)
  }
  
  test("Query Suppliers works") {
    createSchema()
    insertSupplier()
    val results = db.run(suppliers.result).futureValue
    assert(results.size == 3)
    assert(results.head._1 == 49)
  }
  
  after { db.close }
}
