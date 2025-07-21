package com.example.airdry.util

class Enums {

    enum class StackLength {
        L1_8, L1_5, L1_2, L1_0
    }

    enum class StackWidth {
        WIDE, SHORT
    }

    data class StackType(
        val length: StackLength,
        val width: StackWidth
    )
}