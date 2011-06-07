package com.bheap.synapse.utils

import java.io.File

object SynapseScout {
  def getFilesInDirectoryOfType(directory: String, fileType: String) = {
    (new File("/Users/rossputin/.synapse/sites/bheap-example/" + directory)).listFiles.
      filter(_.isFile).filter(_.getName.endsWith("." + fileType))
  }
}
