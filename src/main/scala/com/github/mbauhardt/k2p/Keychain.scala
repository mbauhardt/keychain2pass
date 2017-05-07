package com.github.mbauhardt.k2p

import java.util.Scanner

case class Keychain(keychain: String, entries: List[KeychainEntry])

case class KeychainEntry(kind: String, name: String)

object UnknownEntry extends KeychainEntry("unknown-id", "unknown-name")

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

    }
    keychains.values.toSet
  }
}