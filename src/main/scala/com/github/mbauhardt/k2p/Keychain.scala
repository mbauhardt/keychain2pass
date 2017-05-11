package com.github.mbauhardt.k2p

import java.util.Scanner


trait KeychainEntry {
  def kind: String

  def service: String

  def name: String

  def account: Option[String]
}


object EmptyEntry extends KeychainEntry {
  override def kind: String = ""

  override def service: String = ""

  override def name = ""

  override def account: Option[String] = None
}

case class SecureNoteEntry(service: String) extends KeychainEntry {
  override def kind = "Notes"

  override def name = service

  override def account: Option[String] = None
}

case class WifiPasswordEntry(name: String, username: String) extends KeychainEntry {
  override def kind = "Wifi"

  override def service = "AirPort"

  override def account: Option[String] = Some(username)
}

case class ApplicationPasswordEntry(service: String, username: String) extends KeychainEntry {
  override def kind = "Apps"

  override def name = service

  override def account: Option[String] = Some(username)
}

case class InternetPasswordEntry(service: String, username: String) extends KeychainEntry {
  override def kind = "Websites"

  override def name = service

  override def account: Option[String] = Some(username)
}

case class Keychain(keychain: String, entries: List[KeychainEntry])

object KeychainParser {

  def parseKeychain(dump: String): Set[Keychain] = {

    val scanner = new Scanner(dump)

    val keychains = scala.collection.mutable.LinkedHashMap.empty[String, Keychain]

    var currentKeychain: String = null
    var currentName: String = null
    var currentAccount: String = null
    while (scanner.hasNextLine) {
      val line = scanner.nextLine()

      if (line.startsWith("keychain: ")) {
        // reset var's
        currentKeychain = line.substring(line.indexOf("\"") + 1, line.length - 1)
        currentName = null
        currentAccount = null

        val emptyEntry = EmptyEntry
        val keychain = keychains.getOrElse(currentKeychain, Keychain(currentKeychain, List.empty))

        if (keychain.entries.isEmpty) {
          keychains(currentKeychain) = keychain.copy(entries = List(emptyEntry))
        } else {
          if (keychain.entries.head != EmptyEntry) {
            keychains(currentKeychain) = keychain.copy(entries = emptyEntry :: keychain.entries)
          } else {
            keychains(currentKeychain) = keychain.copy(entries = emptyEntry :: keychain.entries.tail)
          }
        }
      }

      // we take the blob as name only for wifi passwords
      if (line.contains("0x00000007 <blob>=\"")) {
        currentName = line.substring(line.indexOf("=\"") + 2, line.length - 1)
      }

      // user name for wifi or app
      if (line.contains("\"acct\"<blob>=\"")) {
        currentAccount = line.substring(line.indexOf("=\"") + 2, line.length - 1)
      }

      // app or wifi
      if (line.contains("\"svce\"<blob>=\"")) {
        val keychain = keychains.get(currentKeychain).get
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        val service = line.substring(line.indexOf("=\"") + 2, line.length - 1)
        if (service == "AirPort") {
          keychains(currentKeychain) = keychain.copy(entries = WifiPasswordEntry(name = currentName, Option(currentAccount).getOrElse("username not found")) :: tail)
        } else {
          keychains(currentKeychain) = keychain.copy(entries = ApplicationPasswordEntry(service = service, Option(currentAccount).getOrElse("username not found")) :: tail)
        }
      }

      // internet password
      if (line.contains("\"srvr\"<blob>=\"")) {
        val keychain = keychains.get(currentKeychain).get
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        keychains(currentKeychain) = keychain.copy(entries = InternetPasswordEntry(service = line.substring(line.indexOf("=\"") + 2, line.length - 1), Option(currentAccount).getOrElse("username not found"))  :: tail)
      }

      // safe note
      if (line.contains("\"type\"<uint32>=\"note\"")) {
        val keychain = keychains.get(currentKeychain).get
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        keychains(currentKeychain) = keychain.copy(entries = SecureNoteEntry(head.service) :: tail)
      }
    }
    keychains.values.toSet
  }
}