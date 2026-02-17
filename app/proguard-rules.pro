# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
