package com.example.airdry.database.Entities.Stack

import androidx.room.Embedded
import androidx.room.Relation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class NestedStack(
    @Embedded val stack: Stack,
    @Relation(
        parentColumn = "stack_code_id",
        entityColumn = "id"
    )
    val stackCode: StackCode
){
    fun printDimensionsAndVolume(): String {
        val safeDisplayName = stack.displayName.ifBlank { "Unnamed Stack" }
        val safeStackCode = stackCode.displayName.ifBlank { "N/A" }

        try{
            val symbols = DecimalFormatSymbols(Locale.US)
            val dfDim = DecimalFormat("0.000", symbols)
            val dfVol = DecimalFormat("0.00", symbols)
            // --- REPLACEMENT FOR buildString: Triple-quoted string literal ---
            val result = "Stack:$safeDisplayName  Dms:${dfDim.format(stack.length)}×${dfDim.format(stack.height)}×${dfDim.format(stack.thickness)} \n Pieces: ${stack.numberOfPieces}   Cubes: ${dfVol.format(stack.volume)}m³".trim()
            return result

        }catch (e:Exception){
            println("failed to build string at GetStackDTO.printDimensionsAndVolume: ${e.message}")
            println("${stack.length.toDouble()} ${stack.thickness.toDouble()} ${stack.height.toDouble()}")
            return stackCode.displayName
        }
    }
}