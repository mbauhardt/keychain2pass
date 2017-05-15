package com.github.mbauhardt.k2p

import com.lexicalscope.jewel.cli.CliFactory


object Main {

  def main(args: Array[String]): Unit = {

    var options: Options = null
    try {
      options = CliFactory.parseArguments(classOf[Options], args: _*)
    } catch {
      case e: Exception => println(e.getMessage)
        System.exit(-1)
    }

    val dump = Security.dumpKeychain()
    val keychains: Set[Keychain] = KeychainParser.parseKeychain(dump)
    val appPasswords = PassMigration.migrateApplicationPasswords(keychains)
    val internetPasswords = PassMigration.migrateInternetPasswords(keychains)
    val wifiPasswords = PassMigration.migrateWifiPasswords(keychains)
    println(appPasswords)
    println(internetPasswords)
    println(wifiPasswords)
  }
}
