# TFLite
-keep class org.tensorflow.** { *; }
-dontwarn org.tensorflow.**

# ML Kit
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Google Identity
-keep class com.google.android.libraries.identity.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Coil
-keep class coil.** { *; }

# Credential Manager
-keep class androidx.credentials.** { *; }