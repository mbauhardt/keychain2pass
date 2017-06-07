package com.github.mbauhardt.k2p


object Main {

  def main(args: Array[String]): Unit = {
    val dump = Security.dumpKeychain()
    val keychains: Set[Keychain] = KeychainParser.parseKeychain(dump)
    for (keychain <- keychains) {
      Pass.insert(keychain)
    }
  }
}
