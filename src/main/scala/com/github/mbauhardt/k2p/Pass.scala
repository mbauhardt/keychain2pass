package com.github.mbauhardt.k2p

case class Pass(entries: Set[PassEntry])

case class PassEntry(path: String, username: Option[String], password: String)

