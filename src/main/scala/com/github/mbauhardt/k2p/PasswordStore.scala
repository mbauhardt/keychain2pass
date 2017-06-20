package com.github.mbauhardt.k2p

import java.io.{ByteArrayInputStream, File}

import scala.sys.process.{Process, ProcessLogger}

case class PasswordStore(entries: Set[PasswordStoreEntry])

case class PasswordStoreEntry(path: String, username: Option[String], password: String)

object Pass {

  def makeEntriesUnique(entries: List[KeychainEntry]) = {
    def makeEntriesUnique(entries: List[KeychainEntry], acc: List[KeychainEntry]): List[KeychainEntry] = {
      entries match {
        case Nil => acc
        case h :: t => {
          val count = t.count(ke => ke.name == h.name)
          if (count == 0) {
            makeEntriesUnique(t, h :: acc)
          } else {
            val x = h match {
              case internet: InternetPasswordEntry => internet.asInstanceOf[InternetPasswordEntry].copy(name = s"${h.name}($count)")
              case app: ApplicationPasswordEntry => app.asInstanceOf[ApplicationPasswordEntry].copy(name = s"${h.name}($count)")
              case note: SecureNoteEntry => note.asInstanceOf[SecureNoteEntry].copy(name = s"${h.name}($count)")
              case wifi: WifiPasswordEntry => wifi.asInstanceOf[WifiPasswordEntry].copy(name = s"${h.name}($count)")
              case other: KeychainEntry => h
            }
            makeEntriesUnique(t, x :: acc)
          }
        }
      }
    }

    makeEntriesUnique(entries, List.empty)
  }

  def insert(keychain: Keychain) = {
    println
    println
    println(s"Migrate keychain ${keychain.keychain}")
    println("===========================================================================================================")
    val log = ProcessLogger.apply(new File("keychain2pass.log"))
    val nonEmptyEntries = keychain.entries.filter(ke => ke != EmptyEntry)
    val size = nonEmptyEntries.size
    var counter = 0
    val uniqueEntries = makeEntriesUnique(nonEmptyEntries)
    uniqueEntries.foreach(keychainEntry => {
      counter = counter + 1
      val command = keychainEntry match {
        case internet: InternetPasswordEntry => insertInetCommandArgs(keychain, keychainEntry)
        case app: ApplicationPasswordEntry => insertAppCommandArgs(keychain, keychainEntry)
        case note: SecureNoteEntry => insertNoteCommandArgs(keychain, keychainEntry)
        case wifi: WifiPasswordEntry => insertWifiCommandArgs(keychain, keychainEntry.asInstanceOf[WifiPasswordEntry])
      }

      val status = Process(s"pass insert ${command._1} -m").#<(command._2).!(log)
      val percentage = counter * 100 / size
      if (status == 0) {
        //printf("[%d]   Successfully added %-30s to pass folder \t%s\r\n", percentage, "'" + keychainEntry.name + "'", "'" + command._1 + "'")
        println(s"[$percentage%] Successfully added '${keychainEntry.name}' to pass folder '${command._1}'")
      } else {
        println(s"[$percentage%] Failed to add '${keychainEntry.name}' \t\t\tto pass folder '${command._1}'")
      }
    }
    )
    log.close()
  }


  private def insertWifiCommandArgs(keychain: Keychain, keychainEntry: WifiPasswordEntry) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    val account = keychainEntry.account.get
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\nlogin: $account\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    (s"$nameOfKeychain/Wifi/${name.replace(" ", "-")}", pwdInput)
  }

  private def insertNoteCommandArgs(keychain: Keychain, keychainEntry: KeychainEntry) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    (s"$nameOfKeychain/Notes/${name.replace(" ", "-")}", pwdInput)
  }

  private def insertAppCommandArgs(keychain: Keychain, keychainEntry: KeychainEntry) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    val account = keychainEntry.account.get
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\nlogin: $account\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    (s"$nameOfKeychain/Apps/${name.replace(" ", "-")}", pwdInput)
  }

  private def insertInetCommandArgs(keychain: Keychain, keychainEntry: KeychainEntry) = {
    val pwd = keychainEntry.password
    val name = keychainEntry.name
    val account = keychainEntry.account.get
    var pwdInput = new ByteArrayInputStream(s"$pwd\r\nlogin: $account\r\n".getBytes())
    val nameOfKeychain = keychain.keychain
    (s"$nameOfKeychain/Websites/${name.replace(" ", "-")}", pwdInput)
  }
}

