package com.example.candlesrush.models

data class PlaceOrderModel(
    var userid:String?="",
    var userName:String?="",
    var orderid:String?="",
    var productImage:String?="",
    var productName:String?="",
    var productDes:String?="",
    var productPrice: Int?=0,
    var productColor:String?="",
    var productFragrance:String?="",
    var productShape:String?="",
    var productSize:String?=""
    )
