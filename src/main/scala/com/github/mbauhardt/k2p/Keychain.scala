package com.github.mbauhardt.k2p

import java.util.Scanner

case class Keychain(keychain: String, entries: List[KeychainEntry])

case class KeychainEntry(kind: String, name: String, account: Option[String])

object UnknownEntry extends KeychainEntry("unknown-kind", "unknown-name", None)

object KeychainParser {

  def parseKeychain(dump: String): Set[Keychain] = {
    val scanner = new Scanner(dump)

    val keychains = scala.collection.mutable.LinkedHashMap.empty[String, Keychain]

    var currentKeychain: String = null
    while (scanner.hasNextLine) {
      val line = scanner.nextLine()

      if (line.startsWith("keychain: ")) {
        currentKeychain = line.substring(line.indexOf("\"") + 1, line.length - 1)
        val keychainEntry = UnknownEntry
        val keychain = keychains.getOrElse(currentKeychain, Keychain(currentKeychain, List.empty))
        keychains(currentKeychain) = keychain.copy(entries = keychainEntry :: keychain.entries)
      }

      if (line.contains("0x00000007 <blob>=")) {
        val keychain = keychains.get(currentKeychain).get
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        keychains(currentKeychain) = keychain.copy(entries = head.copy(name = line.substring(line.indexOf("\"") + 1, line.length - 1)) :: tail)
      }

      if (line.startsWith("class: \"genp\"")) {
        val keychain = keychains.get(currentKeychain).get
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        keychains(currentKeychain) = keychain.copy(entries = head.copy(kind = "Apps") :: tail)
      }

      if (line.contains("\"type\"<uint32>=\"note\"")) {
        val keychain = keychains.get(currentKeychain).get
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        keychains(currentKeychain) = keychain.copy(entries = head.copy(kind = "Notes") :: tail)
      }

      if (line.contains("class: \"inet\"")) {
        val keychain = keychains.get(currentKeychain).get
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        keychains(currentKeychain) = keychain.copy(entries = head.copy(kind = "Websites") :: tail)
      }

      if (line.contains("\"acct\"<blob>=\"")) {
        val keychain = keychains.get(currentKeychain).get
        val head = keychain.entries.head
        val tail = keychain.entries.tail
        keychains(currentKeychain) = keychain.copy(entries = head.copy(account = Some(line.substring(line.indexOf("=\"") + 2, line.length - 1))) :: tail)
      }
    }
    keychains.values.toSet
  }
}