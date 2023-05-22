package net.itsmeeandrew.aoe4esports.common

fun timeout(seconds: Int) {
    for (i in seconds downTo 0) {
        val loadingBar = "#".repeat(seconds - i) + "-".repeat(i)
        print("Timeout: [$loadingBar]\r")
        Thread.sleep(1000)
    }
    print("\n")
}