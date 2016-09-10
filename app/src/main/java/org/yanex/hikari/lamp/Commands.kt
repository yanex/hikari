package org.yanex.hikari.lamp

import kotlin.Byte.Companion.MIN_VALUE as MIN

open class Command(val checkSumByte: Int? = null, vararg val cmd: Byte)
class CommandWithRandom(checkSumByte: Int? = null, val counterByte: Int, vararg cmd: Byte) : Command(checkSumByte, *cmd)

object Commands {
    val TURN_ON =  Command(null, -86, 10, -4, 58, -122, 1, 10, 1, 1, 0, 40, 13)
    val TURN_OFF = Command(null, -86, 10, -4, 58, -122, 1, 10, 1, 0, 1, 40, 13)

    fun onOff(newState: Boolean) = if (newState) TURN_ON else TURN_OFF

    fun setBrightness(value: Int): Command {
        assert(value >= 0 && value <= 9)
        val cmd = byteArrayOf(-86, 10, -4, 58, -122, 1, 12, 1, ((value + 2) and 0xFF).toByte(), 0, 50, 13)
        return CommandWithRandom(checkSumByte = cmd.lastIndex - 1, counterByte = 9, cmd = *cmd)
    }

    fun setWhiteTemperature(value: Int): Command {
        assert(value >= 0 && value <= 9)
        val cmd = byteArrayOf(-86, 10, -4, 58, -122, 1, 14, 1, ((value + 2) and 0xFF).toByte(), 0, 55, 13)
        return CommandWithRandom(checkSumByte = cmd.lastIndex - 1, counterByte = 9, cmd = *cmd)
    }

    fun rgbReset(): Command {
        val cmd = byteArrayOf(-86, 10, -4, 58, -122, 1, 13, 6,
                1, MIN, MIN, MIN,
                MIN, MIN, /* rnd */ 0, /* sum */ 0, 13)
        return CommandWithRandom(
                checkSumByte = cmd.lastIndex - 1,
                counterByte = cmd.lastIndex - 2,
                cmd = *cmd)
    }
    
    fun rgbColor(color: Int): CommandWithRandom {
        val rgb = color and 0xFFFFFF
        val cmd = byteArrayOf(-86, 10, -4, 58, -122, 1, 13, 6,
                1, (rgb shr 16).toByte(), ((rgb and 0xFFFF) shr 8).toByte(), (rgb and 0xFF).toByte(),
                32, 48, /* rnd */ 0, /* sum */ 0, 13)
        return CommandWithRandom(
                checkSumByte = cmd.lastIndex - 1,
                counterByte = cmd.lastIndex - 2,
                cmd = *cmd)
    }

    fun whiteReset(): Command {
        val cmd = byteArrayOf(-86, 10, -4, 58, -122, 1, 13, 6,
                2, MIN, MIN, MIN,
                MIN, MIN, 0, 0, 13)
        return CommandWithRandom(
                checkSumByte = cmd.lastIndex - 1,
                counterByte = cmd.lastIndex - 2,
                cmd = *cmd)
    }
}