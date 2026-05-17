package com.codexcraft.fileflow.legal

object PrivacyPolicyText {
    const val TEXT = """
# Privacy Policy

Effective Date: January 1, 2024

FileFlow ("we", "us", or "our") respects your privacy and is committed to protecting your personal data. This Privacy Policy explains how we handle your information when you use our mobile application.

## 1. Information We Collect
FileFlow is primarily a local file management tool. 
- **Local Files:** We do not upload your local files to our servers unless you explicitly use the "Firebase Sync" feature.
- **Biometric Data:** Biometric authentication (fingerprint/face) is handled by the Android system. We never access or store your actual biometric data.
- **Firebase Sync:** If you enable sync, we store your favorite file metadata and basic profile information in Firebase.

## 2. Permissions
We require the following permissions to function:
- **Storage/All Files Access:** To manage your files.
- **Biometric:** To secure your Vault.
- **Internet:** For Firebase sync and crash reporting.

## 3. Data Security
Your Vault files are encrypted using AES-256 GCM with keys stored in the Android Keystore System.

## 4. Contact Us
If you have any questions, please contact us at support@codexcraft.com.
    """
}
