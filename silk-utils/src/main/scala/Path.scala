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

import java.io.File

/** Recursively deletes a given directory.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object Path {

  def deleteAll(file: File) {
    def deleteFile(dfile: File) {
      if (dfile.isDirectory) {
        val subfiles = dfile.listFiles
        if (subfiles != null)
        subfiles foreach (deleteFile(_))
      }
      dfile.delete
    }
    deleteFile(file)
  }
}
