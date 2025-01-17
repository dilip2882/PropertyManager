package propertymanager.presentation.components.location

sealed class DialogType {
    object ADD_COUNTRY : DialogType()
    object EDIT_COUNTRY : DialogType()
    object ADD_STATE : DialogType()
    object EDIT_STATE : DialogType()
    object ADD_CITY : DialogType()
    object EDIT_CITY : DialogType()
    object ADD_SOCIETY : DialogType()
    object EDIT_SOCIETY : DialogType()
    object ADD_BLOCK : DialogType()
    object EDIT_BLOCK : DialogType()
    object DELETE_BLOCK : DialogType()
    object ADD_TOWER : DialogType()
    object EDIT_TOWER : DialogType()
    object DELETE_TOWER : DialogType()
    object ADD_FLAT : DialogType()
    object EDIT_FLAT : DialogType()
    object DELETE_FLAT : DialogType()
}
