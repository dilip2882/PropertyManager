package propertymanager.core.network.demo

import android.content.Context
import java.io.InputStream
import javax.inject.Inject

fun interface DemoAssetManager {
    fun open(fileName: String): InputStream
}

class DemoAssetManagerImpl @Inject constructor(
    private val context: Context,
) : DemoAssetManager {

    override fun open(fileName: String): InputStream {
        return context.assets.open(fileName)
    }
}
