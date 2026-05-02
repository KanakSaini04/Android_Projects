package com.codexcraft.caretap.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResultLauncher

object VoiceSearchHelper {

    // Launch Hindi voice recognizer
    fun launchVoiceInput(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "बोलिए...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }
        launcher.launch(intent)
    }

    // Decide what action to take based on voice result
    sealed class VoiceAction {
        data class CallContact(val name: String, val phone: String) : VoiceAction()
        data class OpenApp(val packageName: String, val label: String) : VoiceAction()
        data class GoogleSearch(val query: String) : VoiceAction()
        object NoMatch : VoiceAction()
    }

    fun resolveAction(
        context: Context,
        spokenText: String,
        contacts: List<com.codexcraft.caretap.data.model.Profile>
    ): VoiceAction {
        val text = spokenText.lowercase().trim()

        // 1. Check if it matches a contact name
        val matchedContact = contacts.firstOrNull { contact ->
            val contactName = contact.name.lowercase()
            text.contains(contactName) || contactName.contains(text)
        }
        if (matchedContact != null) {
            return VoiceAction.CallContact(matchedContact.name, matchedContact.phone)
        }

        // 2. Check if it matches an installed app
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = context.packageManager.queryIntentActivities(intent, 0)
        val matchedApp = apps.firstOrNull { appInfo ->
            val appLabel = appInfo.loadLabel(context.packageManager)
                .toString().lowercase()
            text.contains(appLabel) || appLabel.contains(text)
        }
        if (matchedApp != null) {
            return VoiceAction.OpenApp(
                packageName = matchedApp.activityInfo.packageName,
                label = matchedApp.loadLabel(context.packageManager).toString()
            )
        }

        // 3. Default to Google Search
        return if (text.isNotBlank()) {
            VoiceAction.GoogleSearch(spokenText)
        } else {
            VoiceAction.NoMatch
        }
    }

    fun openGoogleSearch(context: Context, query: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
        }
        context.startActivity(intent)
    }

    fun openApp(context: Context, packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent?.let { context.startActivity(it) }
    }
}