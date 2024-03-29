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

import java.io._
import java.util.jar._

/** Help with unpacking from zip/jar archives.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object Archive {

  def extract(file: String) {
    val extractLoc = calculateExtractionLocation(file)
    jarEntries(file) foreach (entry => extractEntry(file, entry, extractLoc))
  }

  def calculateExtractionLocation(file: String) = {
    val jarFile = new JarFile(file)
    lazy val jarFileDir = new File(jarFile.getName).getParentFile
	  lazy val jarFileName = new File(jarFile.getName).getName
	  val jarBasename = jarFileName.substring(0, jarFileName.lastIndexOf("."))
	  val firstEntry = jarFile.entries.nextElement
	  val entryName = firstEntry.getName
	  val isBasenameInEntryPath = entryName.startsWith(jarBasename)

	  if (isBasenameInEntryPath) jarFileDir
	  else new File(jarFileDir, jarBasename)
  }

  def jarEntries(file: String): Iterator[JarEntry] = {
    val jarFile = new JarFile(file)
    val enu = jarFile.entries
    new Iterator[JarEntry] {
      def hasNext = enu.hasMoreElements
      def next = enu.nextElement
    }
  }

  def extractEntry(file: String, jarEntry: JarEntry, todir: File) {
    val jarFile = new JarFile(file)
    val entryPath = jarEntry.getName
    if (jarEntry.isDirectory) {
      new File(todir, entryPath).mkdirs
    } else {
      val istream = jarFile.getInputStream(jarEntry)
      val ostream = new FileOutputStream(new File(todir, entryPath))
      copyStream(istream, ostream)
      ostream.close
      istream.close
    }
  }

  def copyStream(istream: InputStream, ostream: OutputStream) {
    var bytes =  new Array[Byte](1024)
    var len = -1
    while ({ len = istream.read(bytes, 0, 1024); len != -1 })
      ostream.write(bytes, 0, len)
  }
}
