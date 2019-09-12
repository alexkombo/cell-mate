package com.android.cell.mate.ui.model

import org.jetbrains.annotations.NotNull

/**
 * Created by kombo on 2019-09-12.
 */
data class Song(

    @NotNull
    val title: String,

    @NotNull
    val artist: String,

    val releaseYear: Int,

    val rating: Float,

    val isRemix: Boolean
)