package propertymanager.feature.tenant.settings.appereance.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import propertymanager.feature.tenant.settings.appereance.widget.EditTextPreferenceWidget
import propertymanager.feature.tenant.settings.appereance.widget.InfoWidget
import propertymanager.feature.tenant.settings.appereance.widget.ListPreferenceWidget
import propertymanager.feature.tenant.settings.appereance.widget.MultiSelectListPreferenceWidget
import propertymanager.feature.tenant.settings.appereance.widget.SwitchPreferenceWidget
import propertymanager.presentation.components.TextPreferenceWidget
import propertymanager.presentation.util.collectAsState

val LocalPreferenceHighlighted = compositionLocalOf(structuralEqualityPolicy()) { false }
val LocalPreferenceMinHeight = compositionLocalOf(structuralEqualityPolicy()) { 56.dp }

@Composable
fun StatusWrapper(
    item: Preference.PreferenceItem<*>,
    highlightKey: String?,
    content: @Composable () -> Unit,
) {
    val enabled = item.enabled
    val highlighted = item.title == highlightKey
    AnimatedVisibility(
        visible = enabled,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        content = {
            CompositionLocalProvider(
                LocalPreferenceHighlighted provides highlighted,
                content = content,
            )
        },
    )
}

@Composable
internal fun PreferenceItem(
    item: Preference.PreferenceItem<*>,
    highlightKey: String?,
) {
    val scope = rememberCoroutineScope()
    StatusWrapper(
        item = item,
        highlightKey = highlightKey,
    ) {
        when (item) {
            is Preference.PreferenceItem.SwitchPreference -> {
                val value by item.pref.collectAsState()
                SwitchPreferenceWidget(
                    title = item.title,
                    subtitle = item.subtitle,
                    icon = item.icon,
                    checked = value,
                    onCheckedChanged = { newValue ->
                        scope.launch {
                            if (item.onValueChanged(newValue)) {
                                item.pref.set(newValue)
                            }
                        }
                    },
                )
            }
            is Preference.PreferenceItem.SliderPreference -> {
                // TODO: use different composable?
                SliderItem(
                    label = item.title,
                    min = item.min,
                    max = item.max,
                    value = item.value,
                    valueText = item.subtitle.takeUnless { it.isNullOrEmpty() } ?: item.value.toString(),
                    onChange = {
                        scope.launch {
                            item.onValueChanged(it)
                        }
                    },
                )
            }
            is Preference.PreferenceItem.ListPreference<*> -> {
                val value by item.pref.collectAsState()
                ListPreferenceWidget(
                    value = value,
                    title = item.title,
                    subtitle = item.internalSubtitleProvider(value, item.entries),
                    icon = item.icon,
                    entries = item.entries,
                    onValueChange = { newValue ->
                        scope.launch {
                            if (item.internalOnValueChanged(newValue!!)) {
                                item.internalSet(newValue)
                            }
                        }
                    },
                )
            }
            is Preference.PreferenceItem.BasicListPreference -> {
                ListPreferenceWidget(
                    value = item.value,
                    title = item.title,
                    subtitle = item.subtitleProvider(item.value, item.entries),
                    icon = item.icon,
                    entries = item.entries,
                    onValueChange = { scope.launch { item.onValueChanged(it) } },
                )
            }
            is Preference.PreferenceItem.MultiSelectListPreference -> {
                val values by item.pref.collectAsState()
                MultiSelectListPreferenceWidget(
                    preference = item,
                    values = values,
                    onValuesChange = { newValues ->
                        scope.launch {
                            if (item.onValueChanged(newValues)) {
                                item.pref.set(newValues.toMutableSet())
                            }
                        }
                    },
                )
            }
            is Preference.PreferenceItem.TextPreference -> {
                TextPreferenceWidget(
                    title = item.title,
                    subtitle = item.subtitle,
                    icon = item.icon,
                    onPreferenceClick = item.onClick,
                )
            }
            is Preference.PreferenceItem.EditTextPreference -> {
                val values by item.pref.collectAsState()
                EditTextPreferenceWidget(
                    title = item.title,
                    subtitle = item.subtitle,
                    icon = item.icon,
                    value = values,
                    onConfirm = {
                        val accepted = item.onValueChanged(it)
                        if (accepted) item.pref.set(it)
                        accepted
                    },
                )
            }
            is Preference.PreferenceItem.InfoPreference -> {
                InfoWidget(text = item.title)
            }
            is Preference.PreferenceItem.CustomPreference -> {
                item.content()
            }
        }
    }
}
