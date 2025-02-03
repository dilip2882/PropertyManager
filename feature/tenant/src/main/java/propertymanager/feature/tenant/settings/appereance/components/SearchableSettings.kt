package propertymanager.feature.tenant.settings.appereance.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.icerock.moko.resources.StringResource

interface SearchableSettings {

    @Composable
    @ReadOnlyComposable
    fun getTitleRes(): StringResource

    @Composable
    fun getPreferences(): List<Preference>

    @Composable
    fun RowScope.AppBarAction() {
    }


//    PreferenceScaffold(
//    titleRes = getTitleRes(),
//    onBackPressed = if (handleBack != null) handleBack::invoke else null,
//    actions = { AppBarAction() },
//    itemsProvider = { getPreferences() },
//    )
//

    companion object {
        // HACK: for the background blipping thingy.
        // The title of the target PreferenceItem
        // Set before showing the destination screen and reset after
        // See BasePreferenceWidget.highlightBackground
        var highlightKey: String? = null
    }
}
