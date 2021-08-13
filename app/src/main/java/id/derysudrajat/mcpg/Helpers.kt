package id.derysudrajat.mcpg

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette


object Helpers {

    fun getHexTextColor(palette: Palette.Swatch?, context: Context): String =
        String.format(
            "#%06X", 0xFFFFFF and (palette?.titleTextColor ?: getDefaultTextColor(context))
        )

    fun getHexBackgroundColor(palette: Palette.Swatch?, context: Context): String =
        String.format(
            "#%06X", 0xFFFFFF and (palette?.rgb ?: getDefaultBackgroundColor(context))
        )

    private fun getDefaultTextColor(context: Context): Int =
        ContextCompat.getColor(context, R.color.black)

    private fun getDefaultBackgroundColor(context: Context): Int =
        ContextCompat.getColor(context, R.color.white)

    fun String.copyTextToCliBoard(activity: Activity) {
        val text = this
        (activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)
            ?.apply {
                setPrimaryClip(ClipData.newPlainText("Color", text))
            }
        Toast.makeText(activity, "Copying $text to clipboard", Toast.LENGTH_SHORT).show()
    }

}