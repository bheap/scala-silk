package com.bheap.synapse.utils

import java.io.File

object SynapseScout {
  def getFilesInDirectoryOfType(directory: String, fileType: String) = {
    (new File(System.getProperty("user.dir") + "/" + directory)).listFiles.
      filter(_.isFile).filter(_.getName.endsWith("." + fileType))
  }
}
