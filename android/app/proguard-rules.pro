# ProGuard configuration for Hajj & Umrah app
# Protects Islamic content and app security

# Keep all model classes for Islamic content
-keep class com.muslim.hajjrules.model.** { *; }
-keep class com.muslim.hajjrules.data.model.** { *; }

# Keep repository interfaces and implementations
-keep class com.muslim.hajjrules.data.repository.** { *; }
-keep interface com.muslim.hajjrules.data.repository.** { *; }

# Keep domain models and use cases
-keep class com.muslim.hajjrules.domain.** { *; }

# Keep dependency injection classes
-keep class com.muslim.hajjrules.di.** { *; }

# Keep view models
-keep class com.muslim.hajjrules.viewmodel.** { *; }

# Keep security-related classes
-keep class com.muslim.hajjrules.security.** { *; }

# Keep utility classes for Islamic content
-keep class com.muslim.hajjrules.util.** { *; }

# Keep UI accessibility utilities
-keep class com.muslim.hajjrules.ui.** { *; }

# Keep Firebase and Google services classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep Hilt dependency injection
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp

# Keep JSON parsing classes for Islamic content
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep serializable classes
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep parcelable classes
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep R classes and resources
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Arabic font classes
-keep class android.graphics.Typeface { *; }
-keep class android.text.TextPaint { *; }

# Keep accessibility services
-keep class android.view.accessibility.** { *; }
-keep class android.accessibilityservice.** { *; }

# Keep security and encryption classes
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }
-keep class android.security.** { *; }

# Keep network security classes
-keep class javax.net.ssl.** { *; }
-keep class android.net.** { *; }

# Keep SQLite database classes
-keep class android.database.sqlite.** { *; }
-keep class androidx.sqlite.** { *; }

# Keep content providers
-keep class android.content.ContentProvider { *; }

# Keep broadcast receivers
-keep class android.content.BroadcastReceiver { *; }

# Keep services
-keep class android.app.Service { *; }

# Keep application class
-keep class com.muslim.hajjrules.HajjApplication { *; }

# Keep main activity and fragments
-keep class com.muslim.hajjrules.MainActivity { *; }
-keep class com.muslim.hajjrules.fragments.** { *; }

# Keep adapters for Islamic content lists
-keep class com.muslim.hajjrules.adapters.** { *; }

# Remove logging in production builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Optimization for better performance
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Keep crash reporting classes
-keep class com.google.firebase.crashlytics.** { *; }

# Keep analytics classes
-keep class com.google.firebase.analytics.** { *; }

# Keep performance monitoring classes
-keep class androidx.work.** { *; }

# Islamic content specific rules
-keep class org.json.** { *; }
-keep class com.google.gson.** { *; }

# Keep image loading and caching classes
-keep class com.bumptech.glide.** { *; }

# Keep reactive programming classes
-keep class io.reactivex.** { *; }
-keep class io.reactivex.android.** { *; }

# Keep Room database configuration
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Database class *
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# Keep Retrofit networking classes
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep lifecycle components
-keep class androidx.lifecycle.** { *; }
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.AndroidViewModel

# Keep navigation component
-keep class androidx.navigation.** { *; }
-keep class androidx.fragment.app.** { *; }

# Keep custom exception classes
-keep class com.muslim.hajjrules.** extends java.lang.Exception { *; }