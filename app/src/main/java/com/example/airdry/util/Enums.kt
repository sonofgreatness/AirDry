package com.example.airdry.util

class Enums {



    enum class StackType(val displayName : String,
                         val breadth: Float,
                         val width: Float,
                         val height: Float,
                         val numberOfPieces: Int) {

        STACKTYPE_T1_5(Constants.STACKTYPE_T1_5, 1.5f, 0.076f, 0.025f, 10),
        STACKTYPE_T1_8(Constants.STACKTYPE_T1_8, 1.8f, 0.076f, 0.025f, 12),

        STACKTYPE_T2_2(Constants.STACKTYPE_T2_2, 1.2f, 0.100f, 0.019f, 14),
        STACKTYPE_T2_0(Constants.STACKTYPE_T2_0, 1.0f, 0.100f, 0.019f, 13),

        STACKTYPE_T3_0(Constants.STACKTYPE_T3_0, 1.0f, 0.076f, 1.976f, 16),
        STACKTYPE_T3_2(Constants.STACKTYPE_T3_2, 1.2f, 0.076f, 1.976f, 18),
        STACKTYPE_T3_5(Constants.STACKTYPE_T3_5, 1.5f, 0.076f, 1.976f, 20);
        companion object {
            fun fromDisplayName(name: String): StackType =
                StackType.entries.firstOrNull { it.displayName == name }
                    ?: throw IllegalArgumentException("Unknown StackType name: $name")
        }

    }
}