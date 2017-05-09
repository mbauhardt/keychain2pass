package com.github.mbauhardt.k2p

import scala.sys.process.{Process, ProcessLogger}
import scala.util.Try

object Security {

  def findGenericPassword(keychainEntry: KeychainEntry): Try[String] = {
    if (keychainEntry.account.isDefined) {
      findGenericPassword(keychainEntry.name, keychainEntry.account.get)
    } else {
      findGenericPassword(keychainEntry.name)
    }
  }

  def findGenericPassword(service: String): Try[String] = {
    var logger = ProcessLogger(line => ())
    Try(Process(s"security find-generic-password -s $service -w").!!(logger))
  }

  def findGenericPassword(service: String, account: String): Try[String] = {
    var logger = ProcessLogger(line => ())
    Try(Process(s"security find-generic-password -s $service -a $account -w").!!(logger))
  }

  def dumpKeychain(): String = {
    Process("security dump-keychain").!!
  }
}
