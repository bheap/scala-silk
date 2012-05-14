/**
 * Copyright (C) 2011-2012 Bheap Ltd - http://www.bheap.co.uk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.silkyweb.utils

import scala.io.Source
import scala.util.control.Exception._

import com.codecommit.antixml.{Attributes, Elem, Selector, XML => AXML}

import java.io.{File, FileNotFoundException}

import javax.xml.stream.XMLStreamException

/** Some handy utilities for querying and manipulating antixml.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object XML {

  /** Searches an XML block for elements with a given name and attribute, where attribute value contains a signature. */
  def findElements(xml: Elem, elem: Symbol, attr: String, sig: String) = {
    xml \\ elem select Selector {case item: Elem if verifyAttribute(item.attrs, attr, sig) => item }
  }

  /** A safe check for an attribute value. */
  def verifyAttribute(attrs: Attributes, attr: String, sig: String) = {
    if (attrs.contains(attr)) {
	    attrs(attr).contains(sig)
    } else false
  }

  def lookupChainedSource(files: List[String]): Either[Throwable, Elem] = {
    val lookupFile = files collectFirst validSource
    val validSourceState = for { sourceFile <- lookupFile } yield loadResource(new File(sourceFile)) 
    validSourceState getOrElse Left(new Exception("Sorry, something went wrong with your installation of Silk"))
  }

  def isValidSource(file: String): Boolean = {
    loadResource(new File(file)) match {
      case Left(error) =>
        error match {
          case x: FileNotFoundException => false
          case _ => true
        }
      case Right(data) => true
    }
  }

  val validSource: PartialFunction[String, String] = {
    case x if (isValidSource(x)) => x
  }

  def loadResource(file: File): Either[Throwable, Elem] = {
    type FNFE = FileNotFoundException
	  type XSE = XMLStreamException

    val result: Either[Throwable, Elem] = 
      catching(classOf[FNFE], classOf[XSE], classOf[Exception]) either AXML.fromSource(Source.fromFile(file))

    result match {
      case Left(error) =>
        error match {
          case x: XSE => Left(new MalformedSourceException(x.getMessage, file.toString))
          case x: FNFE => Left(x)
          case x: Exception => Left(x)
        }
      case Right(data) =>
        val body = data \\ "body"
        val templateEls = findElements(data, 'div, "id", "silk-view")
        if (data.name != "html") {
          Left(new MalformedSourceException("Structure of source file is bad, needs 'html' as root.", file.toString))
        } else if (body.size < 1) {
          Left(new MalformedSourceException("Structure of source file is bad, needs 'body' inside root 'html'.", file.toString))
        } else if ((body.head.children.filter(_.isInstanceOf[Elem]).size > 1) && (templateEls.size < 1)) {
          Left(new MalformedSourceException("Structure of source file is bad, 'body' should contain only one element e.g. 'div'.", file.toString))
        } else {
          Right(data)
        }
    }
  }
}
