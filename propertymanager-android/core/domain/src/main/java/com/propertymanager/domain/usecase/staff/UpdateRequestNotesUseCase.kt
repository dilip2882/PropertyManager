package com.propertymanager.domain.usecase.staff

import com.propertymanager.common.utils.Response
import com.propertymanager.domain.repository.StaffRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateRequestNotesUseCase @Inject constructor(
    private val repository: StaffRepository
) {
    suspend operator fun invoke(requestId: String, notes: String): Flow<Response<Boolean>> =
        repository.updateRequestNotes(requestId, notes)
}
