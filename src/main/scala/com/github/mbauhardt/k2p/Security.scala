package com.github.mbauhardt.k2p

import scala.sys.process.{Process, ProcessLogger}
import scala.util.Try

object Security {

  def findInternetPassword(keychainEntry: KeychainEntry): Try[String] = {
    if (keychainEntry.account.isDefined) {
      findInternetPassword(keychainEntry.service, keychainEntry.account.get)
    } else {
      findInternetPassword(keychainEntry.service)
    }
  }

  def findGenericPassword(keychainEntry: KeychainEntry): Try[String] = {
    if (keychainEntry.account.isDefined) {
      findGenericPassword(keychainEntry.service, keychainEntry.account.get)
    } else {
      findGenericPassword(keychainEntry.service)
    }
  }

  val logger = ProcessLogger(line => ())

  def findInternetPassword(service: String): Try[String] = {
    Try(Process(s"security find-internet-password -s $service -w").!!(logger))
  }

  def findInternetPassword(service: String, account: String): Try[String] = {
    Try(Process(s"security find-internet-password -s $service -a $account -w").!!(logger))
  }

  def findGenericPassword(service: String): Try[String] = {
    Try(Process(s"security find-generic-password -s $service -w").!!(logger))
  }

  def findGenericPassword(service: String, account: String): Try[String] = {
    Try(Process(s"security find-generic-password -s $service -a $account -w").!!(logger))
  }

  def dumpKeychain(): String = {
    Process("security dump-keychain").!!
  }
}
