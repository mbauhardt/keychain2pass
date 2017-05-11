package com.github.mbauhardt.k2p

import org.scalatest.FunSuite

import scala.util.{Failure, Success}

class PassSuite extends FunSuite {

  test("migrate keychain application password with success function") {
    val keychain = new Keychain("/Library/System/Keychains/login.keychain", List(ApplicationPasswordEntry("Screenhero", "username"), InternetPasswordEntry("github.com", "username")))
    val pass = PassMigration.migratePasswords(Set(keychain), ke => Success("password"), {case entry: ApplicationPasswordEntry => entry})
    assert(pass.entries.size === 1)
    assert(pass.entries.head.path === "/Library/System/Keychains/login.keychain/Apps/Screenhero")
    assert(pass.entries.head.username === Some("username"))
    assert(pass.entries.head.password === "password")
  }


  test("migrate keychain application password with failure function") {
    val keychain = new Keychain("/Library/System/Keychains/login.keychain/apps", List(ApplicationPasswordEntry("Screenhero", "username")))
    val pass = PassMigration.migratePasswords(Set(keychain), ke => Failure(new RuntimeException("password not found")), {case entry: ApplicationPasswordEntry => entry})
    assert(pass.entries.size === 0)
  }
}
