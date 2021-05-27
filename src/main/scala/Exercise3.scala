import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.basic.DatabasePublisher
import slick.jdbc.H2Profile.api._

// The main application
object Exercise3 {
    
    val db = Database.forConfig("h2mem1")
    val suppliers = TableQuery[Suppliers]
    val coffees = TableQuery[Coffees]

    def stokingCheck() :Seq[(String, String, Int)] = {
        val searchinfo = for {
            c <- coffees if c.sales > 0
            s <- suppliers if c.supID === s.id 
        }yield (s.name, c.name, c.sales)
        Await.result(db.run(searchinfo.result), Duration.Inf)
    }

    def printList() = {
        for(t <- stokingCheck()) println(" Supplier Name: " +t._1+ ", Coffee Name: " +t._2+ ", Quantity: "+ t._3)
    }

}