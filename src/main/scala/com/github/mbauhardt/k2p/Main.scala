package com.github.mbauhardt.k2p

import java.nio.charset.Charset


object Main {

  def main(args: Array[String]): Unit = {
    if (args.length == 0) {
      println("Usage: scala keychain2pass_2.12-0.1.1.jar [-h | -enc <ENCODING>]")
      return
    }
    if (args.length == 1 && args(0) == "-h") {
      println("Usage: scala keychain2pass_2.12-0.1.1.jar -enc <ENCODING>")
      println()
      println("Where <enc> is one of the following values: " + Charset.availableCharsets().values())
      return
    }
    require(args.length == 2, "Usage: scala keychain2pass_2.12-0.1.1.jar -enc <ENCODING>")
    val option = args(0)
    val enc = args(1)
    require(option == "-enc", "Usage: scala keychain2pass_2.12-0.1.1.jar -enc <ENCODING>")
    require(Charset.isSupported(enc), s"charset $enc not supported")

    val keychains = Security.listKeychains()
    for (keychain <- keychains) {
      val dump = Security.dumpKeychain(keychain)
      val parsedKeychains: Set[Keychain] = KeychainParser.parseKeychain(dump, enc)
      for (parsedKeychain <- parsedKeychains) {
        Pass.insert(parsedKeychain)
      }
    }
  }
}
