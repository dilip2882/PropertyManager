package propertymanager.core.network.di

import android.content.Context
import com.propertymanager.common.utils.Dispatcher
import com.propertymanager.common.utils.PropertyManagerDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import propertymanager.core.network.demo.DemoAssetManager
import propertymanager.core.network.demo.DemoAssetManagerImpl
import propertymanager.core.network.demo.DemoLocationDataSource
import propertymanager.core.network.demo.LocationDataSource

@Module
@InstallIn(SingletonComponent::class)
object LocationDataSourceModule {

    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    @Provides
    fun provideDemoAssetManager(@ApplicationContext context: Context): DemoAssetManager {
        return DemoAssetManagerImpl(context)
    }

    @Provides
    fun provideLocationDataSource(
        @Dispatcher(PropertyManagerDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        networkJson: Json,
        assets: DemoAssetManager
    ): LocationDataSource {
        return DemoLocationDataSource(ioDispatcher, networkJson, assets)
    }
}
