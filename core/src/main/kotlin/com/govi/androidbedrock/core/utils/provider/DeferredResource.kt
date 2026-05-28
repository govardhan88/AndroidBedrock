package com.govi.androidbedrock.core.utils.provider

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

/**
 * Represents a resource that can be resolved later using a [ResourceProvider].
 * This allows ViewModels to hold resource logic without a Context.
 */
sealed interface DeferredResource<out T> {
    fun resolve(provider: ResourceProvider): T
}

/**
 * Deferred String resource supporting simple strings, formatted strings, and plurals.
 */
sealed class DeferredString : DeferredResource<String> {
    data class Resource(@StringRes val resId: Int, val args: List<Any> = emptyList()) : DeferredString() {
        override fun resolve(provider: ResourceProvider): String =
            if (args.isEmpty()) provider.getString(resId) else provider.getString(resId, *args.toTypedArray())
    }

    data class Plural(@PluralsRes val resId: Int, val quantity: Int, val args: List<Any> = emptyList()) : DeferredString() {
        override fun resolve(provider: ResourceProvider): String =
            provider.getQuantityString(resId, quantity, *args.toTypedArray())
    }

    data class Raw(val value: String) : DeferredString() {
        override fun resolve(provider: ResourceProvider): String = value
    }
}

/**
 * Deferred Color resource supporting single colors and ColorStateLists.
 */
sealed class DeferredColor : DeferredResource<Int> {
    data class Resource(@ColorRes val resId: Int) : DeferredColor() {
        override fun resolve(provider: ResourceProvider): Int = provider.getColor(resId)
    }

    data class StateList(@ColorRes val resId: Int) : DeferredResource<ColorStateList?> {
        override fun resolve(provider: ResourceProvider): ColorStateList? = provider.getColorStateList(resId)
    }

    data class Raw(val value: Int) : DeferredColor() {
        override fun resolve(provider: ResourceProvider): Int = value
    }
}

/**
 * Deferred Drawable resource.
 */
sealed class DeferredDrawable : DeferredResource<Drawable?> {
    data class Resource(@DrawableRes val resId: Int) : DeferredDrawable() {
        override fun resolve(provider: ResourceProvider): Drawable? = provider.getDrawable(resId)
    }

    data class Raw(val value: Drawable?) : DeferredDrawable() {
        override fun resolve(provider: ResourceProvider): Drawable? = value
    }
}

// Extension functions for easy creation

fun @receiver:StringRes Int.deferred(vararg args: Any) = DeferredString.Resource(this, args.toList())
fun @receiver:PluralsRes Int.deferredPlural(quantity: Int, vararg args: Any) = DeferredString.Plural(this, quantity, args.toList())
fun String.deferred() = DeferredString.Raw(this)

fun @receiver:ColorRes Int.deferredColor() = DeferredColor.Resource(this)
fun @receiver:ColorRes Int.deferredColorStateList() = DeferredColor.StateList(this)

fun @receiver:DrawableRes Int.deferredDrawable() = DeferredDrawable.Resource(this)
