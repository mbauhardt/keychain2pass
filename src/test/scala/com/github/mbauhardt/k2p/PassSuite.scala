package com.github.mbauhardt.k2p

import org.scalatest.FunSuite

class PassSuite extends FunSuite {

  test("make entries unique") {
    val entries = List(
      ApplicationPasswordEntry("skype", "username", "password"),
      ApplicationPasswordEntry("skype", "username2", "password2"),
      ApplicationPasswordEntry("screenhero", "username", "password"),
      ApplicationPasswordEntry("itunes", "username", "password"))
    val uniqueNames = Pass.makeEntriesUnique(entries).map(ke => ke.name)
    assert(uniqueNames(0) == "itunes")
    assert(uniqueNames(1) == "screenhero")
    assert(uniqueNames(2) == "skype")
    assert(uniqueNames(3) == "skype(1)")
  }
}
