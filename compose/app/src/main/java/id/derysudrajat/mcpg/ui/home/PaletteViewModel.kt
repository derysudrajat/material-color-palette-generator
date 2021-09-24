package id.derysudrajat.mcpg.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.launch

class PaletteViewModel : ViewModel() {

    var paletteItems = mutableStateListOf<Palette.Swatch?>()
        private set

    private fun addItem(palette: Palette.Swatch?) = paletteItems.add(palette)

    private fun removeAll() = paletteItems.clear()

    var currentImage = mutableStateOf("")
        private set

    private fun setImage(img: String) {
        currentImage.value = img
    }

    fun getPalette(img: String, context: Context) = viewModelScope.launch {
        setImage(img)
        removeAll()

        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(img)
            .allowHardware(false)
            .build()

        val result = (loader.execute(request) as SuccessResult).drawable
        val bitmap = (result as BitmapDrawable).bitmap

        val dominant = createPaletteSync(bitmap).dominantSwatch
        val lightVibrant = createPaletteSync(bitmap).lightVibrantSwatch
        val vibrant = createPaletteSync(bitmap).vibrantSwatch
        val darkVibrant = createPaletteSync(bitmap).darkVibrantSwatch
        val lightMuted = createPaletteSync(bitmap).lightMutedSwatch
        val muted = createPaletteSync(bitmap).mutedSwatch
        val darkMuted = createPaletteSync(bitmap).darkMutedSwatch

        listOf(
            dominant, lightVibrant, vibrant, darkVibrant, lightMuted, muted, darkMuted
        ).forEach { addItem(it) }
    }

    private fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()

}