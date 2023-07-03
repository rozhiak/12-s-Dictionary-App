package com.rmblack.vocabularyof12sgrade.models

@kotlinx.serialization.Serializable
enum class ReviewType{
    REVIEW_ALL,
    REVIEW_MISTAKES
}

@kotlinx.serialization.Serializable
data class ReviewIntentDataPack(val title: String,
                                val reviewType: ReviewType,
                                val oneM :Boolean?,
                                val twoM: Boolean?,
                                val threeM: Boolean?,
                                val moreM: Boolean?)