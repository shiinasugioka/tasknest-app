package edu.uw.ischool.shiina12.tasknest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class PreferencesActivity : AppCompatActivity() {

    private val tag = "PreferencesActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "PreferencesActivity Launched")
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private val tag = "Preferences"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // Set up listeners for checkbox preferences
            val appCheckBox = findPreference<CheckBoxPreference>("notification_app")
            val emailCheckBox = findPreference<CheckBoxPreference>("notification_email")
            val smsCheckBox = findPreference<CheckBoxPreference>("notification_sms")

            val preferences = listOf(appCheckBox, emailCheckBox, smsCheckBox)

            for (preference in preferences) {
                preference?.setOnPreferenceChangeListener { _, newValue ->
                    // Store the new value in SharedPreferences
                    savePreference(preference.key, newValue as Boolean)
                    Log.i(tag, "current selected preference " + preference.key)

                    // Uncheck other preferences in the group
                    preferences.filter { it != preference }.forEach { it?.isChecked = false }

                    true
                }
            }
        }

        private fun savePreference(key: String?, value: Boolean) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = sharedPreferences.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }
    }

}


