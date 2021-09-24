package id.derysudrajat.mcpg

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import coil.annotation.ExperimentalCoilApi
import com.github.dhaval2404.imagepicker.ImagePicker
import id.derysudrajat.mcpg.ui.home.GeneratorBody
import id.derysudrajat.mcpg.ui.home.PaletteViewModel
import id.derysudrajat.mcpg.ui.theme.MaterialColorPaletteGeneratorTheme

@ExperimentalCoilApi
class MainActivity : ComponentActivity() {
    private val model by viewModels<PaletteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.getPalette(img = getString(R.string.sampleImage), this)
        setContent {
            MaterialColorPaletteGeneratorTheme {
                GeneratorBody(
                    model.paletteItems,
                    onAddImage = { addNewImage() },
                    model.currentImage.value, this
                )
            }
        }
    }

    private fun addNewImage() =
        ImagePicker.with(this).createIntent { activityForResult.launch(it) }

    private val activityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        result.handleImagePicker(this) { model.getPalette(it.toString(), this) }
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
            else -> Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
        }
    }
}
