package com.govi.androidbedrock.core.ui.component.nointernet

/**
 * Design format choices for displaying the offline / No Internet state.
 */
enum class NoInternetFormat {
    /**
     * Centered Material 3 modal alert dialog with rounded iconography and actions.
     * This is the default format.
     */
    Dialog,

    /**
     * Full-screen replacement layout with hero illustration, title, body, and actions.
     * Ideal for blocking screens or full-page error states.
     */
    FullScreen,

    /**
     * Slide-up modal bottom sheet with drag handle, title, body, and actions.
     */
    BottomSheet,

    /**
     * Non-intrusive animated sticky banner anchored at the top of the content.
     * Ideal for cached/offline screens where the user can still interact with existing content.
     */
    Banner,

    /**
     * Floating Material 3 snackbar anchored at the bottom of the screen.
     */
    Snackbar,
}
