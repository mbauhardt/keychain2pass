package com.github.mbauhardt.k2p

import java.util.Scanner

case class Keychain(keychain: String, entries: List[KeychainEntry])

case class KeychainEntry(kind: String, name: String)

object UnknownEntry extends KeychainEntry("unknown-kind", "unknown-name")

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
    }
    keychains.values.toSet
  }
}