import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.basic.DatabasePublisher
import slick.jdbc.H2Profile.api._
import slick.basic.DatabasePublisher

object Exercise  {
  
    val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]
    val coffees: TableQuery[Coffees] = TableQuery[Coffees]

    def updateCoffeeTable(updateCoffeeName:String, updateCoffeeSales:Int)(implicit db : Database)={
        val sum1 = Await.result(db.run(coffees.filter(_.name === updateCoffeeName).map(_.sales).result),Duration.Inf)
        val sum2 = updateCoffeeSales + sum1.head
        Await.result(db.run(coffees.filter(_.name === updateCoffeeName).map(_.sales).update(sum2)),Duration.Inf)
    }

    def replenishment()(implicit db : Database):Seq[(String, String, Int)] = {
        val organize = for {
            c <- coffees if c.sales > 0
            s <- suppliers if c.supID === s.id 
        }yield (s.name, c.name, c.sales)
        Await.result(db.run(organize.result), Duration.Inf)
    }

    def printList()(implicit db: Database)={
        println("\n---------------Replenishment list---------------------") 
        for(t <- replenishment()) println(" Supplier Name: " +t._1+ ", Coffee Name: " +t._2+ ", Quantity: "+ t._3)
        println("------------------------------------------------------\n")
    }

}