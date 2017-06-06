package com.github.mbauhardt.k2p

import com.lexicalscope.jewel.cli.CliFactory


object Main {

  def main(args: Array[String]): Unit = {

    var options: Options = null
    try {
      options = CliFactory.parseArguments(classOf[Options], args: _*)
    } catch {
      case e: Exception => println(e.getMessage)
        System.exit(-1)
    }

    val dump = Security.dumpKeychain()
    val keychains: Set[Keychain] = KeychainParser.parseKeychain(dump)
    /** val keychain = Keychain("/Users/mb/Library/Keychains/login.keychain", List(
      * InternetPasswordEntry("google.com", "user", "pwd"),
      * ApplicationPasswordEntry("skype", "user", "pwd"),
      * SecureNoteEntry("my notes", "text"),
      * WifiPasswordEntry("network.lan", "user", "pwd"),
      * EmptyEntry))
      */
    for (keychain <- keychains) {
      Pass.insert(keychain)
    }

  }
}
