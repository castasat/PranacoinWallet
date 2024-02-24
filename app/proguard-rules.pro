# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes *Annotation*

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions**

# okhttp
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# crashlytics reporting
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# rxjava
-keep class io.reactivex.rxjava3.disposables.RunnableDisposable
-dontwarn org.reactivestreams.FlowAdapters
-dontwarn org.reactivestreams.**
-dontwarn java.util.concurrent.Flow*
-dontwarn java.util.concurrent.Flow.**
-dontwarn java.util.concurrent.**

 # Keep generic signature of RxJava3 (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Flowable
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Maybe
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Observable
 -keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Single

-dontoptimize