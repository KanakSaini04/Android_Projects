package com.codexcraft.fileflow.core.legal

val PrivacyPolicyMarkdown = """
# Privacy Policy

**Effective Date:** January 1, 2026  
**App Name:** FileFlow

Welcome to FileFlow. Your privacy is central to our design. This Privacy Policy explains how FileFlow handles your information, how your files are processed, and what limited account-related information may be used when optional sign-in features are enabled.

## 1. Privacy-First Principles

FileFlow is designed as a privacy-first local utility. As a core principle:

- **100% of file processing occurs locally on your device**
- **File contents are not uploaded to our servers**
- **We do not scan, sell, analyze, or monetize the contents of your files**
- File management, reading, conversion, encryption, duplicate detection, and most tooling are performed on-device

This means your documents, images, videos, audio, archives, notes, and other content remain under your control unless you explicitly choose to share them.

## 2. Information We Collect

### 2.1 File Content
FileFlow does **not** collect or upload the contents of your files. File contents remain local to your device except where you explicitly initiate a sharing action, export action, local network transfer, or cloud-linked feature.

### 2.2 Authentication Data
If you choose to sign in using Firebase Authentication, FileFlow may store the following limited account data:

- Email address
- Firebase user ID (UID)

This data is used solely for user account identity and optional metadata synchronization features such as:

- Favorites
- Recent items history
- Preferences or settings linked to your account
- Optional cloud-sync status indicators

We do **not** use authentication data for advertising.

### 2.3 Diagnostic and Technical Data
FileFlow may store limited local technical preferences and app state data such as:

- Theme settings
- Sort preferences
- Last opened locations
- Vault metadata
- Pinned/favorite references
- Recycle Bin metadata
- Local feature settings

Where cloud sync is enabled, only minimal metadata needed for that feature may be associated with your account.

## 3. Permissions We Request

FileFlow requests only the permissions necessary to provide its core functionality.

### 3.1 File Access / Storage Access
FileFlow uses Android's **Scoped Storage** and **Storage Access Framework (SAF)** to help you browse, open, organize, move, rename, and manage files and folders that you explicitly grant access to.

Where applicable, the app may request broad file management access or equivalent capabilities to support advanced file-management operations across user-selected storage locations. Such access, if requested on supported devices, is used strictly for file management functions initiated by you.

**Why this access is needed:**
- Browse folders you select
- Read and manage files
- Rename, move, copy, and delete files
- Create folders and documents
- Support universal reading and local tools

### 3.2 Biometric Permission
FileFlow uses biometric authentication, where available, to protect access to the **Secure Vault**.

**Why biometric access is needed:**
- Authenticate the device owner before opening the vault
- Reduce unauthorized access
- Support secure user verification for sensitive vault actions

Biometric data itself is processed by Android's secure system APIs. FileFlow does not receive or store your fingerprint or face template.

### 3.3 Network Access
If enabled, network access may be used for:
- Firebase authentication
- Metadata sync
- FlowShare local Wi-Fi serving
- Optional update or connectivity checks

File contents are not uploaded to us as part of standard app usage.

## 4. How FileFlow Processes Files

FileFlow processes files locally for features such as:

- File browsing and organization
- PDF viewing
- Media playback
- Text and markdown reading
- Quick editing of plain text
- Image-to-PDF conversion
- Duplicate and cache detection
- Vault encryption/decryption

These operations happen on your device using Android system APIs and app-local logic.

## 5. Secure Vault and Encryption

The Secure Vault is designed to protect sensitive content stored within the app's secure area.

### 5.1 Encryption Standard
FileFlow uses **AES-256-GCM** encryption for vault-protected content.

### 5.2 Key Protection
Encryption keys are managed using the **Android Keystore System**. On supported devices, keys may be hardware-backed, meaning key material is protected by secure hardware components where available.

### 5.3 Biometric Gating
Vault access may require biometric authentication or other device-secure authentication before protected actions are allowed.

### 5.4 Security Limitations
While we apply strong modern security practices, no software security model can guarantee absolute protection under all circumstances. Device compromise, rooted environments, malware, OS vulnerabilities, or disabled screen-lock protections may reduce security.

## 6. Cloud and Sync Features

If you enable account-based features, FileFlow may synchronize limited metadata such as:

- Favorites
- Recents
- Preferences
- Cloud connection status

We do **not** sync or upload the contents of your files as part of our standard metadata sync functionality unless you explicitly enable a feature that requires such transfer.

## 7. Sharing and FlowShare

If you use **FlowShare**, FileFlow can expose selected files over your local Wi-Fi network through a browser-accessible interface.

By using FlowShare, you acknowledge:

- Access is available to devices on the same network as configured
- You are responsible for ensuring your network is trusted
- FileFlow cannot control third-party network interception risks outside the device

We recommend using FlowShare only on trusted local networks.

## 8. Data Retention

Local metadata is retained on your device until you remove it, clear app data, uninstall the app, or delete specific entries.

If cloud-linked metadata sync is enabled, associated synced metadata may remain linked to your account until:
- You delete the synced data
- You delete your account
- Retention is otherwise required for legitimate security or legal reasons

## 9. Your Choices and Controls

You can:
- Use the app without uploading file contents
- Revoke granted folder or document access in system settings
- Disable or avoid login features
- Delete favorites, recents, or vault items
- Remove app data through Android settings
- Uninstall the app at any time

## 10. Children's Privacy

FileFlow is not directed to children under the minimum digital age required by applicable law. We do not knowingly collect personal information from children beyond minimal account identifiers used in optional authentication features.

## 11. Third-Party Services

FileFlow may use third-party services such as:
- Firebase Authentication
- Android system biometric APIs
- Android Keystore
- Local network server components for FlowShare

Your use of those services may also be subject to their own terms and privacy notices.

## 12. International Use

If you use FileFlow in a jurisdiction with specific privacy rights, you may have rights relating to access, deletion, correction, or restriction of personal information associated with your optional account data, subject to applicable law.

## 13. Changes to This Policy

We may update this Privacy Policy from time to time. Updated versions will be made available in the app. Continued use of FileFlow after changes become effective constitutes acceptance of the updated policy.

## 14. Contact

If you have questions regarding this Privacy Policy, please contact the FileFlow support channel listed in the app or official distribution page.

## 15. Summary

In short:
- Your file contents stay local
- We do not upload file contents by default
- Optional login uses only limited identity data for metadata sync
- Vault content is protected with AES-256-GCM and Android Keystore
- Biometric access helps secure sensitive areas
""".trimIndent()
