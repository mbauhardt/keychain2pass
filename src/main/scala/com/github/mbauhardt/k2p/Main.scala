package com.github.mbauhardt.k2p

import java.nio.charset.Charset


object Main {

  def main(args: Array[String]): Unit = {
    require(args.length == 2, "Usage: main -enc <ENCODING>")
    val option = args(0)
    val enc = args(1)
    require(option == "-enc", "Usage: main -enc <ENCODING>")
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
