package com.govi.androidbedrock.core.utils.provider

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for providing Android resources without leaking Context into ViewModels or Repositories.
 * Highly useful for multi-module projects and unit testing business logic.
 */
interface ResourceProvider {
    // Strings
    fun getString(@StringRes stringRes: Int): String
    fun getString(@StringRes stringRes: Int, vararg formatArgs: Any): String
    fun getStringArray(@ArrayRes arrayRes: Int): Array<String>
    fun getQuantityString(@PluralsRes pluralsRes: Int, quantity: Int, vararg formatArgs: Any): String

    // Colors
    fun getColor(@ColorRes colorRes: Int): Int
    fun getColorStateList(@ColorRes colorRes: Int): ColorStateList?

    // Drawables
    fun getDrawable(@DrawableRes drawableRes: Int): Drawable?

    // Values
    fun getDimension(@DimenRes dimenRes: Int): Float
    fun getInteger(@IntegerRes intRes: Int): Int
    fun getBoolean(@BoolRes boolRes: Int): Boolean

    // Resolver
    fun <T> resolve(deferred: DeferredResource<T>): T = deferred.resolve(this)
}

/**
 * Default implementation of [ResourceProvider] using the application context.
 */
@Singleton
class DefaultResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {

    override fun getString(stringRes: Int): String = context.getString(stringRes)

    override fun getString(stringRes: Int, vararg formatArgs: Any): String =
        context.getString(stringRes, *formatArgs)

    override fun getStringArray(arrayRes: Int): Array<String> =
        context.resources.getStringArray(arrayRes)

    override fun getQuantityString(pluralsRes: Int, quantity: Int, vararg formatArgs: Any): String =
        context.resources.getQuantityString(pluralsRes, quantity, *formatArgs)

    override fun getColor(colorRes: Int): Int =
        ContextCompat.getColor(context, colorRes)

    override fun getColorStateList(colorRes: Int): ColorStateList? =
        ContextCompat.getColorStateList(context, colorRes)

    override fun getDrawable(drawableRes: Int): Drawable? =
        ContextCompat.getDrawable(context, drawableRes)

    override fun getDimension(dimenRes: Int): Float =
        context.resources.getDimension(dimenRes)

    override fun getInteger(intRes: Int): Int =
        context.resources.getInteger(intRes)

    override fun getBoolean(boolRes: Int): Boolean =
        context.resources.getBoolean(boolRes)
}
