package com.govi.androidbedrock.core.utils.compose.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.govi.androidbedrock.core.utils.provider.DefaultResourceProvider
import com.govi.androidbedrock.core.utils.provider.DeferredResource

/**
 * Resolves a [DeferredResource] within a Composable function.
 */
@Composable
@ReadOnlyComposable
fun <T> DeferredResource<T>.resolve(): T {
    val context = LocalContext.current
    val provider = DefaultResourceProvider(context)
    return resolve(provider)
}
