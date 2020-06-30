import org.scalatest.{FunSuite, Matchers}

class HelloScalaTest extends FunSuite with Matchers {

  test("testSayHello") {
    HelloScala.sayHello should be ("Hello world!")
  }

}
