package com.manasnap.dto.scryfall

import com.fasterxml.jackson.annotation.JsonProperty

data class ImageUris(
    val png: String?
)

data class ScryfallCardResponse(
    val name: String,
    @JsonProperty("image_uris")
    val imageUris: ImageUris?
)
