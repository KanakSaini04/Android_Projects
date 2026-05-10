package com.codexcraft.lensora.core.util

object Constants {
    // Navigation routes
    const val ROUTE_SPLASH = "splash"
    const val ROUTE_AUTH = "auth"
    const val ROUTE_PERMISSIONS = "permissions"
    const val ROUTE_MAIN = "main"

    // Tab indices
    const val TAB_CAMERA = 0
    const val TAB_GALLERY = 1
    const val TAB_EDIT = 2
    const val TAB_SETTINGS = 3

    // DataStore keys
    const val PREFS_USER_NAME = "user_name"
    const val PREFS_USER_EMAIL = "user_email"
    const val PREFS_USER_PHOTO = "user_photo"
    const val PREFS_IS_AUTHENTICATED = "is_authenticated"
    const val PREFS_PERMISSIONS_GRANTED = "permissions_granted"

    // AI Pulse interval
    const val AI_PULSE_INTERVAL_MS = 10_000L

    // Aspect ratio
    const val ASPECT_RATIO_W = 9
    const val ASPECT_RATIO_H = 16

    // App Links
    const val PRIVACY_POLICY_URL = "https://codexcraft.in/lensora/privacy"
    const val TERMS_URL = "https://codexcraft.in/lensora/terms"

    // Google Sign-In Web Client ID — replace with your actual ID
    const val GOOGLE_WEB_CLIENT_ID = "456360598001-jrltef5cslh5hkgfdm1921qf1e25kbj9.apps.googleusercontent.com"

    // Legal text
    val PRIVACY_POLICY_TEXT = """
Last Updated: May 2026. Welcome to Lensora AI, developed by CodexCraft. We believe your memories are yours alone.

1. Data We Collect
We only collect the basic profile information (Name, Email, Profile Picture) provided by your Google Account during sign-in to personalize your app experience.

2. On-Device Processing
All AI enhancements, including the 10s Scene Pulse, Generative Magic Eraser, and Mirror Sync, are processed locally on your device's hardware. We do not upload your camera feed or photos to the cloud.

3. The Biometric Vault
Photos secured in the Vault are encrypted locally. CodexCraft does not have access to your Vault, and losing your device or biometric access may result in permanent loss of these files.

4. Third-Party Services
We utilize Google ML Kit and standard monetization networks which may collect anonymous usage data.

5. Contact
For privacy inquiries, contact CodexCraft directly.
    """.trimIndent()

    val TERMS_AND_CONDITIONS_TEXT = """
Last Updated: May 2026. By using Lensora AI, you agree to these terms:

1. License of Use
CodexCraft grants you a personal, non-exclusive license to use Lensora AI.

2. Content Ownership
You retain full copyright and ownership of all photographs and videos captured and edited using the app.

3. User Conduct
You agree not to use the app to capture, edit, or distribute illegal or harmful content.

4. Limitation of Liability
Lensora AI processes intensive generative tasks. CodexCraft is not liable for device overheating, battery drain, or data loss occurring from the use of the Biometric Vault or local storage management.

5. 'Find' Feature Usage
The 'Find' visual search accesses public internet resources. CodexCraft is not responsible for the content of third-party videos suggested by the app.
    """.trimIndent()
}