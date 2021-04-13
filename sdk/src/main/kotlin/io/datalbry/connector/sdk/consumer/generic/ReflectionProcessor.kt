package io.datalbry.connector.sdk.consumer.generic

import io.datalbry.connector.api.document.DocumentProcessor

internal interface ReflectionProcessor: DocumentProcessor<Any, Any> {

    fun consumes(): Class<*>

    fun produces(): Class<*>

    fun name(): String
}
