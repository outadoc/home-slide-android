package fr.outadoc.mdi

@Target(
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPE,
    AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.SOURCE)
annotation class IconStringRef
