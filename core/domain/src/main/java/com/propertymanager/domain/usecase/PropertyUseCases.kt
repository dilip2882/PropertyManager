package com.propertymanager.domain.usecase

import com.propertymanager.domain.usecase.property.AddAddressUseCase
import com.propertymanager.domain.usecase.property.AddPropertyUseCase
import com.propertymanager.domain.usecase.property.DeleteAddressUseCase
import com.propertymanager.domain.usecase.property.DeletePropertyUseCase
import com.propertymanager.domain.usecase.property.GetPropertiesUseCase
import com.propertymanager.domain.usecase.property.GetPropertyByIdUseCase
import com.propertymanager.domain.usecase.property.UpdateAddressUseCase
import com.propertymanager.domain.usecase.property.UpdatePropertyUseCase

data class PropertyUseCases(
    val getProperties: GetPropertiesUseCase,
    val addProperty: AddPropertyUseCase,
    val deleteProperty: DeletePropertyUseCase,
    val updateProperty: UpdatePropertyUseCase,
    val getPropertyById: GetPropertyByIdUseCase,
    val addAddress: AddAddressUseCase,
    val updateAddress: UpdateAddressUseCase,
    val deleteAddress: DeleteAddressUseCase
)

