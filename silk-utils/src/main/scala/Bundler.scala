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

import java.io.{File, FileInputStream, FileOutputStream, FileWriter, IOException}

/** Put required resources in place for site spinning.
  *
  * Essentially just a targeted directory copy.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object Bundler {

  private val allowedHiddenFiles = Array(".gitignore")

  /** Recursively copy items from a src to a dest. */
  def bundle(src: File, dst: File) {
    if (src.isDirectory) {
      if(!dst.exists()) dst.mkdir

      val files = src.list filterNot {
        file => file.startsWith(".") && !allowedHiddenFiles.contains(file)
      }

      files foreach {
        file =>
          val srcFile = new File(src, file)
          val dstFile = new File(dst, file)
          bundle(srcFile, dstFile)
      }
    } else {
      dst.createNewFile
      dst.getCanonicalFile.getParentFile.mkdirs
      
      bundleFile(src, dst)
    }
  }

  /** Copy a single item from src to dest. */
  // @todo note this ties us to JVM
  def bundleFile(src: File, dst: File) {
    new FileOutputStream(dst) getChannel() transferFrom(
		    new FileInputStream(src) getChannel, 0, Long.MaxValue )
  }
}
