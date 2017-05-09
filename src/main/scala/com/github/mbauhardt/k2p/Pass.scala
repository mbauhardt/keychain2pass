package com.github.mbauhardt.k2p

import scala.util.Try

case class Pass(entries: Set[PassEntry])

case class PassEntry(path: String, username: Option[String], password: String)

object PassMigration {

  def migrateApplicationPasswords(keychains: Set[Keychain], f2: KeychainEntry => Try[String]): Pass = {
    val passEntries = for {
      kc <- keychains
      entries = kc.entries
        // filter all application passwords
        .filter(e => e.kind == "Apps")
        // filter all application passwords with an username
        .filter(e => e.account.isDefined)
        // map to a tuple3 which contains the path, the account and the maybe the password if possible to extract
        .map(e => (kc.keychain + "/" + e.kind + "/" + e.name, e.account.get, f2.apply(e)))
        // filter for all successfully extracted passwords
        .filter(t => t._3.isSuccess)
        // and finally map to a pass entry
        .map(t => new PassEntry(t._1, Some(t._2), t._3.get))
    } yield entries
    new Pass(entries = passEntries.flatten)
  }
}