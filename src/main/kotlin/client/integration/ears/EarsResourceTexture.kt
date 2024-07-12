package gay.pyrrha.mimic.client.integration.ears

import com.unascribed.ears.NativeImageAdapter
import com.unascribed.ears.api.features.EarsFeatures
import com.unascribed.ears.common.EarsCommon
import com.unascribed.ears.common.EarsFeaturesHolder
import com.unascribed.ears.common.EarsFeaturesParser
import com.unascribed.ears.common.render.AbstractEarsRenderDelegate
import com.unascribed.ears.common.util.EarsStorage
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.ResourceTexture
import net.minecraft.util.Identifier

public class EarsResourceTexture(id: Identifier) : ResourceTexture(id), EarsFeaturesHolder {
    private var earsFeatures = EarsFeatures.DISABLED

    public fun processSkin(image: NativeImage) {
        EarsStorage.put(image, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(NativeImageAdapter(image)))

        EarsCommon.carefullyStripAlpha({ x1: Int, y1: Int, x2: Int, y2: Int ->
            stripAlpha(image, x1, y1, x2, y2)
        }, image.height != 32)

        this.earsFeatures = EarsFeaturesParser.detect(
            NativeImageAdapter(image),
            EarsStorage.get(image, EarsStorage.Key.ALFALFA)
        ) { bytes: ByteArray? ->
            NativeImageAdapter(
                NativeImage.read(
                    AbstractEarsRenderDelegate.toNativeBuffer(
                        bytes
                    )
                )
            )
        }
    }

    private fun stripAlpha(image: NativeImage, x1: Int, y1: Int, x2: Int, y2: Int) {
        for (i in x1 until x2) {
            for (j in y1 until y2) {
                image.setColor(i, j, image.getColor(i, j) or -16777216)
            }
        }
    }

    override fun getEarsFeatures(): EarsFeatures {
        return earsFeatures
    }
}