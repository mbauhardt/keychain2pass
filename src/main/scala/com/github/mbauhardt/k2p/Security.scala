package com.github.mbauhardt.k2p

import scala.sys.process.Process

object Security {

  def listKeychains() = {
    val keychains = Process("security list-keychains").!!
    val strings = keychains.split("\"").filter(s => !s.trim.isEmpty)
    strings.toList
  }

  def dumpKeychain(keychain: String): String = {
    Process(s"security dump-keychain -d $keychain").!!
  }
}
