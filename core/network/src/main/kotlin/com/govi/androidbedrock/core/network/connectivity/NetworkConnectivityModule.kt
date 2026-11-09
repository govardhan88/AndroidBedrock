package com.govi.androidbedrock.core.network.connectivity

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkConnectivityModule {
    @Binds
    @Singleton
    fun bindNetworkMonitor(impl: ConnectivityManagerNetworkMonitor): NetworkMonitor
}
