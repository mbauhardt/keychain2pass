package com.github.mbauhardt.k2p

import java.util.Scanner


trait KeychainEntry {
  def kind: String

  def name: String

  def account: Option[String]

  def password: String
}


object EmptyEntry extends KeychainEntry {
  override def kind: String = ""

  override def name = ""

  override def account: Option[String] = None

  override def password = ""
}

case class SecureNoteEntry(name: String, password: String) extends KeychainEntry {
  override def kind = "Notes"

  override def account: Option[String] = None
}

case class WifiPasswordEntry(name: String, username: String, password: String) extends KeychainEntry {
  override def kind = "Wifi"

  override def account: Option[String] = Some(username)
}

case class ApplicationPasswordEntry(name: String, username: String, password: String) extends KeychainEntry {
  override def kind = "Apps"

  override def account: Option[String] = Some(username)
}

case class InternetPasswordEntry(name: String, username: String, password: String) extends KeychainEntry {
  override def kind = "Websites"

  override def account: Option[String] = Some(username)
}

case class Keychain(keychain: String, entries: List[KeychainEntry])

object KeychainParser {

  def parseKeychain(dump: String): Set[Keychain] = {

    val scanner = new Scanner(dump)

    val keychains = scala.collection.mutable.LinkedHashMap.empty[String, Keychain]

    var currentKeychain: String = null
    var wifiName: String = null
    var userName: String = null
    while (scanner.hasNextLine) {
      val line = scanner.nextLine()

      if (line.startsWith("keychain: ")) {

        // reset var's
        currentKeychain = stringBetweenDoubleQuotes(line)
        wifiName = null
        userName = null
      }

      // we take the blob as name only for wifi passwords
      if (line.contains("0x00000007 <blob>=\"")) {
        wifiName = stringBetweenEqualSignAndDoubleQuotes(line)
      }

      // user name for wifi or app
      if (line.contains("\"acct\"<blob>=\"")) {
        userName = stringBetweenEqualSignAndDoubleQuotes(line)
      }

      // app or wifi
      if (line.contains("\"svce\"<blob>=\"")) {
        val keychain = keychains.getOrElseUpdate(currentKeychain, Keychain(currentKeychain, List(EmptyEntry)))
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        val name = stringBetweenEqualSignAndDoubleQuotes(line)
        keychains(currentKeychain) =
          if (name == "AirPort") {
            keychain.copy(entries = WifiPasswordEntry(name = wifiName, Option(userName).getOrElse("username not found"), "password not found") :: head :: tail)
          } else {
            keychain.copy(entries = ApplicationPasswordEntry(name = name, Option(userName).getOrElse("username not found"), "password not found") :: head :: tail)
          }
      }

      // internet password
      if (line.contains("\"srvr\"<blob>=\"")) {
        val keychain = keychains.getOrElseUpdate(currentKeychain, Keychain(currentKeychain, List(EmptyEntry)))
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        val name = stringBetweenEqualSignAndDoubleQuotes(line)
        keychains(currentKeychain) = keychain.copy(entries = InternetPasswordEntry(name, Option(userName).getOrElse("username not found"), "password not found") :: head :: tail)
      }

      // replace application password entry with secure note entry
      if (line.contains("\"type\"<uint32>=\"note\"")) {
        val keychain = keychains.getOrElseUpdate(currentKeychain, Keychain(currentKeychain, List(EmptyEntry)))
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        keychains(currentKeychain) = keychain.copy(entries = SecureNoteEntry(head.name, "password not found") :: tail)
      }

      // parse password out of data
      if (line.startsWith("data")) {
        val pwd = scanner.nextLine()
        val keychain = keychains.getOrElseUpdate(currentKeychain, Keychain(currentKeychain, List(EmptyEntry)))
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        val finalHead: KeychainEntry = head match {
          case internet: InternetPasswordEntry => internet.copy(password = stringBetweenDoubleQuotes(pwd))
          case app: ApplicationPasswordEntry => app.copy(password = stringBetweenDoubleQuotes(pwd))
          case wifi: WifiPasswordEntry => wifi.copy(password = stringBetweenDoubleQuotes(pwd))
          case note: SecureNoteEntry => {
            val i = pwd.indexOf("<string>")
            val j = pwd.indexOf("</string>")
            if (i > -1 && j > -1) {
              note.copy(password = pwd.substring(i + 8, j))
            } else {
              note.copy(password = stringBetweenDoubleQuotes(pwd))
            }
          }
          case other: KeychainEntry => other
        }
        keychains(currentKeychain) = keychain.copy(entries = finalHead :: tail)
      }
    }

    val res = for (key <- keychains.keys; nonEmpty = keychains(key).entries.filter(kc => kc != EmptyEntry)) yield Keychain(key, nonEmpty)
    res.toSet
  }

  private def stringBetweenEqualSignAndDoubleQuotes(s: String) = {
    s.substring(s.indexOf("=\"") + 2, s.length - 1)
  }

  private def stringBetweenDoubleQuotes(s: String) = {
    s.substring(s.indexOf("\"") + 1, s.length - 1)
  }
}