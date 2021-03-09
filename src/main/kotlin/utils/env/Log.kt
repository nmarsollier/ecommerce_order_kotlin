package utils.env

import java.util.logging.Level
import java.util.logging.Logger

object Log {
    fun error(error: Exception) {
        error.printStackTrace()
        Logger.getGlobal().log(Level.SEVERE, error.toString())
    }

    fun info(msg: String) {
        Logger.getGlobal().log(Level.INFO, msg)
    }
}