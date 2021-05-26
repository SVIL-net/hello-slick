import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.basic.DatabasePublisher
import slick.jdbc.H2Profile.api._

//object Excercise2  extends App{
object Excercise2  {

  val db = Database.forConfig("h2mem1")
  //try {

    // The query interface for the Suppliers table
    val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]

    // the query interface for the Coffees table
    val coffees: TableQuery[Coffees] = TableQuery[Coffees]

   def updateCoffeeTable(updateCoffeeName:String, updateCoffeeSales:Int) ={
    Await.result(db.run(coffees.filter(_.name === updateCoffeeName).map(_.sales).update(updateCoffeeSales)),Duration.Inf)
    }
//  } finally db.close
}





