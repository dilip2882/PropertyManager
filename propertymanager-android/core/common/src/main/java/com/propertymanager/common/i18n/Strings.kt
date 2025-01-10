package com.propertymanager.common.i18n

import android.content.Context
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.format

class Strings(
    private val context: Context
) {
    fun get(id: StringResource, args: List<Any>): String {
        return if(args.isEmpty()) {
            StringDesc.Resource(id).toString(context = context)
        } else {
            id.format(*args.toTypedArray()).toString(context)
        }
    }
}
