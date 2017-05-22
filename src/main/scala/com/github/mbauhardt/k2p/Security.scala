package com.github.mbauhardt.k2p

import scala.sys.process.Process

object Security {

  def dumpKeychain(): String = {
    Process("security dump-keychain -d").!!
  }
}
