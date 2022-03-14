package com.example.lib

import com.alibaba.fastjson.annotation.JSONField

class OriginPackageItem: BaseItem() {
    @JSONField(name = "Id")
    var id = 0

    @JSONField(name = "Title")
    var title: String = ""

    @JSONField(name = "SourceUrl")
    var sourceUrl: String = ""

    @JSONField(name = "FileSize")
    var fileSize: String = ""

    @JSONField(name = "PlayTime")
    var playTime: String = ""
}