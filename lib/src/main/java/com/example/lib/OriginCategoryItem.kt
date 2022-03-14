package com.example.lib

import com.alibaba.fastjson.annotation.JSONField

class OriginCategoryItem: BaseItem() {
    @JSONField(name = "Id")
    var id = 0

    @JSONField(name = "Title")
    var title: String = ""

    @JSONField(name = "ImgUrl")
    var imgUrl: String = ""
}