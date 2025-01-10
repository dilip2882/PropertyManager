package propertymanager.core.network.demo

import com.propertymanager.common.utils.Dispatcher
import com.propertymanager.common.utils.PropertyManagerDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import propertymanager.core.network.model.CityData
import propertymanager.core.network.model.CountryData
import propertymanager.core.network.model.StateData
import javax.inject.Inject

class DemoLocationDataSource @Inject constructor(
    @Dispatcher(PropertyManagerDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json,
    private val assets: DemoAssetManager,
) : LocationDataSource {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getCountries(): List<CountryData> =
        withContext(ioDispatcher) {
            assets.open(LOCATION_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getStatesForCountry(countryId: Int): List<StateData> =
        getCountries().find { it.id == countryId }?.states ?: emptyList()

    override suspend fun getCitiesForState(stateId: Int): List<CityData> =
        getCountries().flatMap { it.states }
            .find { it.id == stateId }?.cities ?: emptyList()

    companion object {
        private const val LOCATION_ASSET = "countries+states+cities.json"
    }
}
