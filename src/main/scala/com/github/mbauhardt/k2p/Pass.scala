package com.github.mbauhardt.k2p

import scala.util.Try

case class Pass(entries: Set[PassEntry])

case class PassEntry(path: String, username: Option[String], password: String)

object PassMigration {

  def migrateInternetPasswords(keychains: Set[Keychain]): Pass = {
    migratePasswords(keychains, Security.findInternetPassword, { case entry: InternetPasswordEntry => entry })
  }

  def migrateApplicationPasswords(keychains: Set[Keychain]): Pass = {
    migratePasswords(keychains, Security.findGenericPassword, { case entry: ApplicationPasswordEntry => entry })
  }

  def migratePasswords(keychains: Set[Keychain], f: KeychainEntry => Try[String], p: PartialFunction[KeychainEntry, KeychainEntry]): Pass = {
    val passEntries = for {
      kc <- keychains
      entries = kc.entries
        // filter for specific password type
        .collect(p)
        // filter all application passwords with an username
        .filter(e => e.account.isDefined)
        // map to a tuple3 which contains the path, the account and the maybe the password if possible to extract
        .map(e => (kc.keychain + "/" + e.kind + "/" + e.service, e.account.get, f.apply(e)))
        .filter(t => t._3.isSuccess).map(t => new PassEntry(t._1, Some(t._2), t._3.get))
    } yield entries
    new Pass(entries = passEntries.flatten)
  }
}