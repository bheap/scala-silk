package com.bheap.synapse.utils

import java.io.File

object SynapseScout {
  def getFilesInDirectoryOfType(directory: String, fileType: String) = {
    (new File(System.getProperty("user.home") + "/.synapse/sites/" + System.getProperty("ss") + "/" + directory)).listFiles.
      filter(_.isFile).filter(_.getName.endsWith("." + fileType))
  }
}
