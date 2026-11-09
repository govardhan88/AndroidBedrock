package com.govi.androidbedrock.core.base.dispatcher.di

import com.govi.androidbedrock.core.base.dispatcher.AppDispatchers
import com.govi.androidbedrock.core.base.dispatcher.DefaultAppDispatchers
import com.govi.androidbedrock.core.base.dispatcher.DefaultDispatcher
import com.govi.androidbedrock.core.base.dispatcher.IoDispatcher
import com.govi.androidbedrock.core.base.dispatcher.MainDispatcher
import com.govi.androidbedrock.core.base.dispatcher.MainImmediateDispatcher
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DispatchersBindingModule {
    @Binds
    @Singleton
    fun bindAppDispatchers(impl: DefaultAppDispatchers): AppDispatchers
}

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @MainImmediateDispatcher
    fun provideMainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate
}
