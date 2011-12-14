package com.bheap.silk.utils

import java.io.{File, FileInputStream, FileOutputStream, FileWriter, IOException}

/** Put required resources in place for site spinning.
  *
  * Essentially just a targeted directory copy.
  *
  * @author <a href="mailto:ross@bheap.co.uk">rossputin</a>
  * @since 1.0 */
object SilkBundle {
  def bundle(src: File, dst: File) {
    if (src.isDirectory) {
      if(!dst.exists()) dst.mkdir

      val files = src.list
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

  def bundleFile(src: File, dst: File) {
    new FileOutputStream(dst) getChannel() transferFrom(
		    new FileInputStream(src) getChannel, 0, Long.MaxValue )
  }
}
