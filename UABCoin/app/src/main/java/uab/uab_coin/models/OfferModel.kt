package uab.uab_coin.models

data class OfferModel(
    var offerName: String? = null,
    var offerDescription: String? = null,
    var offerImage: String? = null,
    var offerRedeemCode: String? = null,
    var offerPrice: Int? = null,
)
