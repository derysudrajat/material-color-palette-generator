package id.derysudrajat.mcpg

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import id.derysudrajat.mcpg.Helpers.copyTextToCliBoard
import id.derysudrajat.mcpg.Helpers.getHexBackgroundColor
import id.derysudrajat.mcpg.Helpers.getHexTextColor
import id.derysudrajat.mcpg.databinding.ItemPaletteBinding

class PaletteAdapter(
    private val context: Context,
    private val palettes: List<Palette.Swatch?>
) : RecyclerView.Adapter<PaletteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private lateinit var binding: ItemPaletteBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemPaletteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val palette = palettes[position]
        val textColor = getHexTextColor(palette, context)
        val rgbColor = getHexBackgroundColor(palette, context)

        with(binding) {
            tvName.apply {
                text = listName[position]
                setTextColor(Color.parseColor(textColor))
            }
            tvHex.apply {
                text = rgbColor
                setTextColor(Color.parseColor(textColor))
            }
            itemContent.setBackgroundColor(Color.parseColor(rgbColor))

            btnCopy.apply {
                iconTint = ColorStateList.valueOf(Color.parseColor(textColor))
                setOnClickListener {
                    tvHex.text.toString().copyTextToCliBoard((context as Activity))
                }
            }
        }
    }

    private val listName = listOf(
        "Dominant", "Light Vibrant", "Vibrant", "Dark Vibrant",
        "Light Muted", "Muted", "Dark Muted",
    )

    override fun getItemCount(): Int = palettes.size
}