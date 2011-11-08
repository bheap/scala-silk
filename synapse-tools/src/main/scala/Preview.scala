package com.bheap.synapse.tools

import scala.collection.JavaConversions._

import java.io.File

import name.pachler.nio.file._

class Preview(path: String) {

  val watchService = FileSystems.getDefault.newWatchService
  val viewPath = Paths.get(path + "/view")
  val templatePath = Paths.get(path + "/template")
  val resourcePath = Paths.get(path + "/resource")
  
	val viewKey = viewPath.register(watchService, StandardWatchEventKind.ENTRY_CREATE, StandardWatchEventKind.ENTRY_DELETE)
  val templateKey = templatePath.register(watchService, StandardWatchEventKind.ENTRY_CREATE, StandardWatchEventKind.ENTRY_DELETE)
  val resourceKey = resourcePath.register(watchService, StandardWatchEventKind.ENTRY_CREATE, StandardWatchEventKind.ENTRY_DELETE)

  while (true) {
    // take() will block until a file has been created/deleted
    val signalledKey = watchService.take

    // get list of events from key
    val  eventList = signalledKey.pollEvents

    // VERY IMPORTANT! call reset() AFTER pollEvents() to allow the
    // key to be reported again by the watch service
    signalledKey.reset

    // we'll simply print what has happened; real applications
    // will do something more sensible here
    eventList.foreach {
      e =>
      var message = ""
        if (e.kind == StandardWatchEventKind.ENTRY_CREATE) {
          val context = e.context.asInstanceOf[Path]
          message = path + "/" + context.toString + " event"
          val wd = new File(path)
          val proc = Runtime.getRuntime().exec("synapse build", null, wd)
          Thread.sleep(1000)
          val previewFile = if (context.toString.contains("~")) context.toString.init else context.toString
          val proc2 = Runtime.getRuntime().exec("synapse-preview " + "file://" + path + "/site/" + previewFile, null, wd)
        } else if (e.kind == StandardWatchEventKind.OVERFLOW) {
          message = "OVERFLOW: more changes happened than we could retreive"
        }
        println(message)
	    }
	}
}
