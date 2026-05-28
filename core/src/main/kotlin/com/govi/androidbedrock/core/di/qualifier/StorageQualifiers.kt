package com.govi.androidbedrock.core.di.qualifier

import javax.inject.Qualifier

/**
 * Qualifier for secure storage (EncryptedSharedPreferences).
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SecureStorage
