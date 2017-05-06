package com.github.mbauhardt.k2p

import org.scalatest.FunSuite

class SecuritySuite extends FunSuite {

  test("Dump Keychain") {
    val keychain = Security.dumpKeychain()
    assert(keychain.contains("System.keychain"))
    assert(keychain.contains("login.keychain"))
  }
}
