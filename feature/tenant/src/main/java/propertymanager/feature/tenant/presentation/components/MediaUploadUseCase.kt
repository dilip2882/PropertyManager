package propertymanager.feature.tenant.presentation.components

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.storage.FirebaseStorage
import com.propertymanager.common.utils.Response
import com.propertymanager.domain.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class MediaUploadUseCase @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) {
    fun uploadMedia(uri: Uri, mediaType: MediaType): Flow<Response<String>> = flow {
        emit(Response.Loading)
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "bin"
            val fileName = "${UUID.randomUUID()}.$extension"
            val storageRef = storage.reference
                .child("maintenance_media")
                .child(mediaType.name.lowercase())
                .child(fileName)

            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            emit(Response.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to upload media"))
        }
    }
}

