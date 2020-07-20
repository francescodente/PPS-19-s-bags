package sbags.interaction

import scala.io.StdIn

package object view {

  trait IO[+A] {
    def flatMap[B](f: A => IO[B]): IO[B]
    def map[B](f: A => B): IO[B] = flatMap(x => unit(f(x)))
  }

  def unit[A](a: A): IO[A] = new IO[A] {
    override def flatMap[B](f: A => IO[B]): IO[B] = f(a)
  }

  def write[A](a: A): IO[A] = new IO[A] {
    override def flatMap[B](f: A => IO[B]): IO[B] = {println(a); f(a)}
  }

  def read(): IO[String] = new IO[String] {
    override def flatMap[B](f: String => IO[B]): IO[B] = f(StdIn.readLine())
  }

}
