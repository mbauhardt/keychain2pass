package com.github.mbauhardt.k2p

import java.io.ByteArrayInputStream

import scala.sys.process.Process

case class PasswordStore(entries: Set[PasswordStoreEntry])

case class PasswordStoreEntry(path: String, username: Option[String], password: String)

object Pass {

  def insert(keychain: Keychain) = {
    keychain.entries.foreach(keychainEntry =>
      keychainEntry match {
        case internet: InternetPasswordEntry => insertInet(keychain, keychainEntry)
        case app: ApplicationPasswordEntry => insertApp(keychain, keychainEntry)
        case note: SecureNoteEntry => insertNote(keychain, keychainEntry)
        case wifi: WifiPasswordEntry => insertWifi(keychain, keychainEntry)
        case other: KeychainEntry =>
      })
  }


  private def insertWifi(keychain: Keychain, keychainEntry: KeychainEntry) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    val account = keychainEntry.account.get
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\nlogin: $account\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    val output = Process(s"pass insert $nameOfKeychain/Wifi/${name.replace(" ", "-")} -m").#<(pwdInput).!!
    println(s"${output}")
  }

  private def insertNote(keychain: Keychain, keychainEntry: KeychainEntry) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    val output = Process(s"pass insert $nameOfKeychain/Notes/${name.replace(" ", "-")} -m").#<(pwdInput).!!
    println(s"${output}")
  }

  private def insertApp(keychain: Keychain, keychainEntry: KeychainEntry) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    val account = keychainEntry.account.get
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\nlogin: $account\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    val output = Process(s"pass insert $nameOfKeychain/Apps/${name.replace(" ", "-")} -m").#<(pwdInput).!!
    println(s"${output}")
  }

  private def insertInet(keychain: Keychain, keychainEntry: KeychainEntry) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    val account = keychainEntry.account.get
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\nlogin: $account\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    val output = Process(s"pass insert $nameOfKeychain/Websites/${name.replace(" ", "-")} -m").#<(pwdInput).!!
    println(s"${output}")
  }
}

