# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Keep-annotated classes
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Data Models & DTOs deserialized by Gson
-keep class com.niatmandiwajib.ghusl.domain.model.** { *; }
-keep class com.niatmandiwajib.ghusl.domain.engine.** { *; }
-keep class com.niatmandiwajib.ghusl.data.remote.dto.** { *; }
-keep class com.niatmandiwajib.ghusl.data.local.entity.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers class * {
    kotlin.jvm.internal.DefaultConstructorMarker *;
}
-keepclassmembers enum * { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Generative AI
-keep class com.google.ai.client.generativeai.** { *; }
