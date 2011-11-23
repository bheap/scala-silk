package com.bheap.silk.tools

import scala.collection.JavaConversions._

import java.io.File

import name.pachler.nio.file._

class Preview(path: String) {

  val watchService = FileSystems.getDefault.newWatchService
  val viewPath = Paths.get(path + "/view")
  val templatePath = Paths.get(path + "/template")
  val resourcePath = Paths.get(path + "/resource")
  
  // @todo probably fail on platforms other than OSX, they may be able to detect more than just create events.. rename ?
	val viewKey = viewPath.register(watchService, StandardWatchEventKind.ENTRY_CREATE)
  val templateKey = templatePath.register(watchService, StandardWatchEventKind.ENTRY_CREATE)
  val resourceKey = resourcePath.register(watchService, StandardWatchEventKind.ENTRY_CREATE)

  val pathMap = Map(viewKey -> viewPath, templateKey -> templatePath, resourceKey -> resourcePath)

  val fileFilter = List("html", "css", "js", "jpg", "png", "gif")

  while (true) {
    // take will block until a file has been created
    val signalledKey = watchService.take
    val eventPath = pathMap(signalledKey)

    // get list of events from key
    val  eventList = signalledKey.pollEvents

    // VERY IMPORTANT! call reset() AFTER pollEvents() to allow the
    // key to be reported again by the watch service
    signalledKey.reset

    eventList.foreach {
      e =>
        if (e.kind == StandardWatchEventKind.ENTRY_CREATE) {
          val context = e.context.asInstanceOf[Path]
          if ((context.toString count (_ == '.')) == 1) {
            val fExt = context.toString.split('.').last
            if (fileFilter.contains(fExt)) {
              val wd = new File(path)
              val proc = Runtime.getRuntime().exec("silk build", null, wd)
              if (eventPath.toString.contains("/view")) {
                Thread.sleep(1200)
                val previewFile = if (context.toString.contains("~")) context.toString.init else context.toString
                val proc2 = Runtime.getRuntime().exec("silk-preview " + "file://" + path + "/site/" + previewFile, null, wd)
              } else {
                val indexFile = new File(path)
                if (indexFile.exists) {
                  Thread.sleep(1200)
                  val proc2 = Runtime.getRuntime().exec("silk-preview " + "file://" + path + "/site/index.html", null, wd) 
                }
              }
            }
          }
        }
	    }
	}
}
