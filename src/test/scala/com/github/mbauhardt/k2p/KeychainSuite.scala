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
    assert(keychain.head.entries.length === 1)
    assert(keychain.head.entries(0).kind === "Wifi")
    assert(keychain.head.entries(0).service === "AirPort")
    assert(keychain.head.entries(0).name === "My-Network")
    assert(keychain.head.entries(0).account === Some("My-Network"))

    assert(keychain.tail.head.keychain === "/Library/Keychains/login.keychain")
    assert(keychain.tail.head.entries.length === 4)
    assert(keychain.tail.head.entries(0).kind === "Apps")
    assert(keychain.tail.head.entries(0).service === "Screenhero")
    assert(keychain.tail.head.entries(0).name === "Screenhero")
    assert(keychain.tail.head.entries(0).account === Some("username"))

    assert(keychain.tail.head.entries(1).kind === "Notes")
    assert(keychain.tail.head.entries(1).service === "my safe note")
    assert(keychain.tail.head.entries(1).name === "my safe note")
    assert(keychain.tail.head.entries(1).account === None)

    assert(keychain.tail.head.entries(2).kind === "Websites")
    assert(keychain.tail.head.entries(2).service === "mylogin.com")
    assert(keychain.tail.head.entries(2).name === "mylogin.com")
    assert(keychain.tail.head.entries(2).account === Some("username2"))

    assert(keychain.tail.head.entries(3).kind === "Websites")
    assert(keychain.tail.head.entries(3).service === "mylogin.com")
    assert(keychain.tail.head.entries(3).name === "mylogin.com")
    assert(keychain.tail.head.entries(3).account === Some("username"))
  }
}
