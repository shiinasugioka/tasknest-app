package edu.uw.ischool.shiina12.tasknest.api_util

/**
 * This file contains unique request codes used to identify different
 * requests when receiving results from activities or services.
 *
 * REQUEST_ACCOUNT_PICKER (1000): Used as a request code when launching
 * an activity or fragment to pick a user account.
 *
 * REQUEST_AUTHORIZATION (1001): Used as a request code when initiating
 * an authorization process.
 *
 * REQUEST_GOOGLE_PLAY_SERVICES (1002): Used as a request code when checking
 * or requesting Google Play services. Ensures that the required
 * Google Play services are available for the application.
 *
 * PREF_ACCOUNT_NAME ("tasknest_app"): Represents the key used to store
 * and retrieve the user's account name from shared preferences.
 * Shared preferences are a way to store simple data persistently.
 *
 * APPLICATION_NAME ("TaskNest App"): The name of the application.
 * Use where the application name needs to be referenced.
 *
 */

object Constants {
    const val REQUEST_ACCOUNT_PICKER = 1000
    const val REQUEST_AUTHORIZATION = 1001
    const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    const val PREF_ACCOUNT_NAME = "tasknest_app"
    const val APPLICATION_NAME = "TaskNest App"
}