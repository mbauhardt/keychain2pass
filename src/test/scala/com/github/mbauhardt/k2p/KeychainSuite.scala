package com.github.mbauhardt.k2p

import org.scalatest.FunSuite

class KeychainSuite extends FunSuite {

  test("parse keychain") {
    val resourceFolder = getClass.getClassLoader.getResource("dump.txt").getFile
    val source = scala.io.Source.fromFile(resourceFolder)
    val lines = try source.mkString finally source.close()
    val keychain = KeychainParser.parseKeychain(lines)
    assert(keychain.size === 2)
    assert(keychain.head.keychain === "/Library/Keychains/System.keychain")
    assert(keychain.head.entries.length === 3)
    assert(keychain.tail.head.keychain === "/Library/Keychains/login.keychain")
    assert(keychain.tail.head.entries.length === 3)
  }
}
