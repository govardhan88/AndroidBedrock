package govi.core.base.dispatcher.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import govi.core.base.dispatcher.AppDispatchers
import govi.core.base.dispatcher.DefaultAppDispatchers
import govi.core.base.dispatcher.DefaultDispatcher
import govi.core.base.dispatcher.IoDispatcher
import govi.core.base.dispatcher.MainDispatcher
import govi.core.base.dispatcher.MainImmediateDispatcher
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
