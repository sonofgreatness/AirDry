package com.example.airdry.util

class Enums {



    enum class StackType(val displayName : String) {

        STACKTYPE_T1_5(Constants.STACKTYPE_T1_5),
        STACKTYPE_T1_8(Constants.STACKTYPE_T1_8),
        STACKTYPE_T2_2(Constants.STACKTYPE_T2_2),
        STACKTYPE_T2_0(Constants.STACKTYPE_T2_0),
        STACKTYPE_T3_0(Constants.STACKTYPE_T3_0),
        STACKTYPE_T3_2(Constants.STACKTYPE_T3_2),
        STACKTYPE_T3_5(Constants.STACKTYPE_T3_5);
        companion object {
            fun fromDisplayName(name: String): StackType =
                StackType.entries.firstOrNull { it.displayName == name }
                    ?: throw IllegalArgumentException("Unknown StackType name: $name")
        }

    }
}