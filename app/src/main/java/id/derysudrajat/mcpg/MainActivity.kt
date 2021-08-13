package id.derysudrajat.mcpg

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DefaultItemAnimator
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.RoundedCornersTransformation
import com.github.dhaval2404.imagepicker.ImagePicker
import id.derysudrajat.mcpg.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sampleImage = getString(R.string.sample_image)
        with(binding) {

            updateColorPalette(sampleImage)

            btnAddPhoto.setOnClickListener {
                ImagePicker.with(this@MainActivity)
                    .createIntent { activityForResult.launch(it) }
            }

        }
    }

    private fun updateColorPalette(sampleImage: String) = lifecycleScope.launch {
        getPalette(sampleImage).let {
            val textColor = Helpers.getHexTextColor(it[0], this@MainActivity)
            val rgbColor = Helpers.getHexBackgroundColor(it[0], this@MainActivity)

            with(binding) {
                imageView.load(sampleImage) {
                    crossfade(true)
                    transformations(RoundedCornersTransformation(8f))
                }

                rvPalette.apply {
                    itemAnimator = DefaultItemAnimator()
                    adapter = PaletteAdapter(this@MainActivity, it)
                }

                btnAddPhoto.apply {
                    imageTintList = ColorStateList.valueOf(Color.parseColor(textColor))
                    backgroundTintList = ColorStateList.valueOf(Color.parseColor(rgbColor))
                }

                contentMain.setBackgroundColor(
                    Color.parseColor(rgbColor.replace("#", "#80"))
                )

                val darkVibrantColor = Helpers.getHexBackgroundColor(it[3], this@MainActivity)

                window.apply {
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    statusBarColor = Color.parseColor(darkVibrantColor)
                }

                val vibrantColor = Helpers.getHexBackgroundColor(it[2], this@MainActivity)
                val vibrantText = Helpers.getHexTextColor(it[2], this@MainActivity)

                val mainTitle = getString(R.string.title_camera)

                supportActionBar?.apply {
                    setBackgroundDrawable(ColorDrawable(Color.parseColor(vibrantColor)))
                    val source = "<font color='$vibrantText'>$mainTitle</font>"
                    title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
                    else Html.fromHtml(source)
                }


            }
        }
    }


    private suspend fun getPalette(img: String): List<Palette.Swatch?> {
        val loader = ImageLoader(this@MainActivity)
        val request = ImageRequest.Builder(this@MainActivity)
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

        return listOf(
            dominant, lightVibrant, vibrant, darkVibrant, lightMuted, muted, darkMuted
        )
    }

    private fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()

    private val activityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        result.handleImagePicker(this) { updateColorPalette(it.toString()) }
    }

    private fun ActivityResult.handleImagePicker(
        context: Context,
        onResult: (Uri) -> Unit
    ) {
        when (this.resultCode) {
            Activity.RESULT_OK -> onResult(this.data?.data ?: "".toUri())
            ImagePicker.RESULT_ERROR -> Toast.makeText(
                context as Activity,
                ImagePicker.getError(this.data), Toast.LENGTH_SHORT
            ).show()
            else -> Toast.makeText(context, getString(R.string.cancel), Toast.LENGTH_SHORT).show()
        }
    }
}