package com.govi.androidbedrock.core.di

import com.govi.androidbedrock.core.di.qualifier.DefaultDispatcher
import com.govi.androidbedrock.core.di.qualifier.IoDispatcher
import com.govi.androidbedrock.core.di.qualifier.MainDispatcher
import com.govi.androidbedrock.core.di.qualifier.UnconfinedDispatcher
import com.govi.androidbedrock.core.utils.DefaultDispatchersProvider
import com.govi.androidbedrock.core.utils.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DispatcherModule {

    @Binds
    @Singleton
    abstract fun bindDispatchersProvider(
        defaultDispatchersProvider: DefaultDispatchersProvider
    ): DispatchersProvider

    companion object {
        @Provides
        @MainDispatcher
        fun provideMainDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
            dispatchersProvider.main()

        @Provides
        @IoDispatcher
        fun provideIoDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
            dispatchersProvider.io()

        @Provides
        @DefaultDispatcher
        fun provideDefaultDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
            dispatchersProvider.default()

        @Provides
        @UnconfinedDispatcher
        fun provideUnconfinedDispatcher(dispatchersProvider: DispatchersProvider): CoroutineDispatcher =
            dispatchersProvider.unconfined()
    }
}
