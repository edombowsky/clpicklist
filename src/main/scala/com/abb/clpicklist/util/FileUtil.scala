package com.abb.clpicklist.util

import better.files.File


object FileUtil {

  /**
   * Returns a list of files that have specific extensions
   *
   * @param dir Directory to find files in
   * @param extensions List of files extensions to filter files with
   * @return List of files in the passed in directory that have any of the passed in extensions
   */
  def getListOfFiles(dir: String, extensions: List[String]): List[File] = {
    val d = File(dir)
    if (d.exists && d.isDirectory) {
      d.list.filter(_.isRegularFile).toList
      d.list.filter(_.isRegularFile).toList.filter {
        file => extensions.exists(file.name.endsWith(_))
      }
    } else {
      List[File]()
    }
  }
}
