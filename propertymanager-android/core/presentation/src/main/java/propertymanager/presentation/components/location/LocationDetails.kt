package propertymanager.presentation.components.location

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.propertymanager.domain.model.location.Block
import com.propertymanager.domain.model.location.City
import com.propertymanager.domain.model.location.Country
import com.propertymanager.domain.model.location.Flat
import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.model.location.State
import com.propertymanager.domain.model.location.Tower

@Composable
fun LocationDetails(
    modifier: Modifier = Modifier,
    selectedItem: Any?,
    onEdit: (Any) -> Unit,
    onDelete: (Any) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        selectedItem?.let { item ->
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getItemTitle(item),
                    style = MaterialTheme.typography.headlineMedium
                )
                Column {
                    IconButton(onClick = { onEdit(item) }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                    IconButton(onClick = { onDelete(item) }) {
                        Icon(Icons.Default.Delete, "Delete")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ItemDetails(item = item)
        }
    }
}

@Composable
private fun ItemDetails(item: Any) {
    when (item) {
        is Country -> CountryDetails(item)
        is State -> StateDetails(item)
        is City -> CityDetails(item)
        is Society -> SocietyDetails(item)
        is Block -> BlockDetails(item)
        is Tower -> TowerDetails(item)
        is Flat -> FlatDetails(item)
    }
}

@Composable
private fun CountryDetails(country: Country) {
    DetailItem(null, country.name)
//    DetailItem("Name", country.name)
//    DetailItem("ISO3", country.iso3)
//    DetailItem("ISO2", country.iso2)
//    DetailItem("Phone Code", country.phoneCode)
//    DetailItem("Capital", country.capital)
//    DetailItem("Currency", country.currency)
//    DetailItem("Region", country.region)
}

@Composable
private fun StateDetails(state: State) {
    DetailItem(null, state.name)
//    DetailItem("Name", state.name)
//    DetailItem("State Code", state.stateCode)
//    DetailItem("Type", state.type)
}

@Composable
private fun CityDetails(city: City) {
    DetailItem(null, city.name)
//    DetailItem("Name", city.name)
//    DetailItem("Latitude", city.latitude)
//    DetailItem("Longitude", city.longitude)
}

@Composable
private fun SocietyDetails(society: Society) {
    DetailItem(null, society.name)
//    DetailItem("Name", society.name)
//    DetailItem("Latitude", society.latitude)
//    DetailItem("Longitude", society.longitude)
}

@Composable
private fun BlockDetails(block: Block) {
    DetailItem(null, block.name)
//    DetailItem("Name", block.name)
//    DetailItem("Type", block.type)
}

@Composable
private fun TowerDetails(tower: Tower) {
    DetailItem(null, tower.name)
//    DetailItem("Name", tower.name)
}

@Composable
private fun FlatDetails(flat: Flat) {
    DetailItem(null, flat.number)
//    DetailItem("Number", flat.number)
//    DetailItem("Floor", flat.floor.toString())
//    DetailItem("Type", flat.type)
//    DetailItem("Area", flat.area.toString())
//    DetailItem("Status", flat.status)
}

@Composable
private fun DetailItem(label: String?, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(120.dp)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun getItemTitle(item: Any): String {
    return when (item) {
        is Country -> "Country"
        is State -> "State"
        is City -> "City"
        is Society -> "Society"
        is Block -> "Block"
        is Tower -> "Tower"
        is Flat -> "Flat "
        else -> "Details"
    }
} 
