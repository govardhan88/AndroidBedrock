package com.govi.androidbedrock.core.network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityManagerNetworkMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : NetworkMonitor {
    private val connectivityManager: ConnectivityManager? = context.getSystemService()

    override val isCurrentlyOnline: Boolean
        get() {
            val cm = connectivityManager ?: return false
            val activeNetwork = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

    override val isOnline: Flow<Boolean> =
        callbackFlow {
            val cm = connectivityManager
            if (cm == null) {
                trySend(false)
                close()
                return@callbackFlow
            }

            // Send initial connectivity state
            trySend(isCurrentlyOnline)

            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    private val networksWithInternet = mutableSetOf<Network>()

                    override fun onAvailable(network: Network) {
                        val capabilities = cm.getNetworkCapabilities(network)
                        val hasInternet =
                            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        if (hasInternet) {
                            networksWithInternet.add(network)
                        }
                        trySend(networksWithInternet.isNotEmpty() || isCurrentlyOnline)
                    }

                    override fun onLost(network: Network) {
                        networksWithInternet.remove(network)
                        trySend(networksWithInternet.isNotEmpty() || isCurrentlyOnline)
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities,
                    ) {
                        val hasInternet =
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                        if (hasInternet) {
                            networksWithInternet.add(network)
                        } else {
                            networksWithInternet.remove(network)
                        }
                        trySend(networksWithInternet.isNotEmpty())
                    }
                }

            val request =
                NetworkRequest
                    .Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

            cm.registerNetworkCallback(request, callback)

            awaitClose {
                cm.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
            .conflate()
}
