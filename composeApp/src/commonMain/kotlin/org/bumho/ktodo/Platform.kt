package org.bumho.ktodo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform