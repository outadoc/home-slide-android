package fr.outadoc.quickhass.feature.slideover.model.annotation

@Target(
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPE,
    AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.SOURCE)
annotation class StringState