package com.bheap.synapse

import scala.io.Source

import com.codecommit.antixml._

import org.scalatest.FunSuite

class Transformers extends FunSuite {
  //test("a simple transformation") {
    //val xml = XML fromSource (Source fromURL (getClass getResource ("/views/bookstore.xml")))

    //println("xml is : " + xml)
    
    // Add a 'first' attribute to the first book element
    //val results = xml \ "book"
    //val book2 = results.head.copy(attrs=Attributes("first" -> "yes"))
    //val results2 = results.updated(0, book2)
    //val xml2 = results2.unselect
    //println("xml2 is : " + xml2)

    // Print a list of titles and the first author
    //val titles = (xml \ "book" \ "title" \ *) map { _.toString }
    //println("titles is : " + titles)
    //val firstAuthor = (xml \\ "author" \ *) map { _.toString } headOption asInstanceOf[Option[String]]
    //println("firstAuthor is : " + firstAuthor.get)

    // Convert all 'title' elements into attributes
    //val extraChild = Group(<book title="Ross extra book"><author>Ross mcDonald</author></book>.anti)
    //println("extraChild is : " + extraChild)
    //val titledBooks = for {
      //bookElem <- xml \ "book"
      //title <- bookElem \ "title" \ text
      //if !title.trim.isEmpty
      //val filteredChildren = bookElem.children filter { case Elem(None, "title", _, _, _) => false case _ => true }
    //} yield bookElem.copy(attrs=(bookElem.attrs + ("title" -> title)), children=filteredChildren)
    //val xml3 = titledBooks.unselect
    //println("xml 3 is : " + xml3)

    // Filter excluding the second book
    //val books = xml \ 'book
    //val bookstore2 = (books filter (books(1) !=)).unselect
    //println("bookstore2 is : " + bookstore2)

    //val fc = (xml \ 'book).head children filter { case Elem(None, "title", _, _, _) => false case _ => true }

    //assert(1 === 1)
  //}
}
