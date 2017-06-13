package com.github.mbauhardt.k2p

import java.io.{ByteArrayInputStream, File}

import scala.sys.process.{FileProcessLogger, Process, ProcessLogger}

case class PasswordStore(entries: Set[PasswordStoreEntry])

case class PasswordStoreEntry(path: String, username: Option[String], password: String)

object Pass {

  def insert(keychain: Keychain) = {
    println
    println
    println(s"Migrate keychain ${keychain.keychain}")
    println("======================================")
    val log = ProcessLogger.apply(new File("keychain2pass.log"))
    val size = keychain.entries.size
    var counter = 0
    keychain.entries.foreach(keychainEntry => {
      counter = counter + 1
      val status = keychainEntry match {
        case internet: InternetPasswordEntry => insertInet(keychain, keychainEntry, log)
        case app: ApplicationPasswordEntry => insertApp(keychain, keychainEntry, log)
        case note: SecureNoteEntry => insertNote(keychain, keychainEntry, log)
        case wifi: WifiPasswordEntry => insertWifi(keychain, keychainEntry, log)
        case other: KeychainEntry =>
      }
      val percentage = counter * 100 / size
      if (status == 0) {
        println(s"[$percentage%] Successfully added '${keychainEntry.name}' to pass folder '${keychain.keychain}'")
      } else {
        println(s"[$percentage%] Failed to add '${keychainEntry.name}' to pass folder '${keychain.keychain}'")
      }
    }
    )
    log.close()
  }


  private def insertWifi(keychain: Keychain, keychainEntry: KeychainEntry, log: FileProcessLogger) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    val account = keychainEntry.account.get
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\nlogin: $account\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    val output = Process(s"pass insert $nameOfKeychain/Wifi/${name.replace(" ", "-")} -m").#<(pwdInput).!(log)
    log.flush()
    output
  }

  private def insertNote(keychain: Keychain, keychainEntry: KeychainEntry, log: FileProcessLogger) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    val output = Process(s"pass insert $nameOfKeychain/Notes/${name.replace(" ", "-")} -m").#<(pwdInput).!(log)
    log.flush()
    output
  }

  private def insertApp(keychain: Keychain, keychainEntry: KeychainEntry, log: FileProcessLogger) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    val account = keychainEntry.account.get
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\nlogin: $account\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    val output = Process(s"pass insert $nameOfKeychain/Apps/${name.replace(" ", "-")} -m").#<(pwdInput).!(log)
    log.flush()
    output
  }

  private def insertInet(keychain: Keychain, keychainEntry: KeychainEntry, log: FileProcessLogger) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    val account = keychainEntry.account.get
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\nlogin: $account\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    val output = Process(s"pass insert $nameOfKeychain/Websites/${name.replace(" ", "-")} -m").#<(pwdInput).!(log)
    log.flush()
    output
  }
}

