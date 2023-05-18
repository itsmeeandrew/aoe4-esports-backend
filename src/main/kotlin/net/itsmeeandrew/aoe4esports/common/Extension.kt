package net.itsmeeandrew.aoe4esports.common

import org.jsoup.select.Elements

fun Elements.getText(): String = this.run {
    this.firstOrNull()?.text()?.trim()
} ?: ""