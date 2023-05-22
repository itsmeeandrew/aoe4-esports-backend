package net.itsmeeandrew.aoe4esports.common

import org.jsoup.select.Elements

fun Elements.getOwnText(): String? = this.run {
    this.firstOrNull()?.ownText()?.trim()
}

fun Elements.getText(): String? = this.run {
    this.firstOrNull()?.text()?.trim()
}