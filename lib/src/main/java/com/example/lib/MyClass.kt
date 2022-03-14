package com.example.lib

import com.example.lib.NetWork.requestWithToken
import kotlin.jvm.JvmStatic
import com.alibaba.fastjson.JSON
import java.net.URL
import java.util.HashMap

object MyClass {
    private var anchorLastId = 0
    private var packageLastId = 0

    val dir = "package"


    @JvmStatic
    fun main(args: Array<String>) {
        //        一级分类
        val newCategoryList = arrayListOf<CategoryItem>()
        val paramMap: MutableMap<String, Any> = HashMap()
        paramMap["lid"] = 0;
        paramMap["cid"] = 0;
        paramMap["pi"] = 0;
        requestWithToken(NetWork.CATEGORY, JSON.toJSONString(paramMap), object : NetworkCallback {
            override fun callback(item: BaseItem) {
                val dataString = item.data
                val dataJson = JSON.parseObject(dataString)
                val contentStr = JSON.toJSONString(dataJson["content"]) ?: return;
                val categoryList = JSON.parseArray(contentStr, OriginCategoryItem::class.java)
                for (originCategory: OriginCategoryItem in categoryList) {
                    requestAnchorList(0, originCategory.id, object : ArrayListCallback<AnchorItem> {
                        override fun callback(list: List<AnchorItem>) {
                            val newCategoryItem = CategoryItem()
                            newCategoryItem.id = originCategory.id
                            newCategoryItem.title = originCategory.title
                            newCategoryItem.imgUrl = originCategory.imgUrl
                            newCategoryItem.anchorList = list
                            newCategoryList.add(newCategoryItem)
                        }
                    })
                    break
                }
                FileTool.writeFile("$dir/packages.json", JSON.toJSONString(newCategoryList))
            }
        })

    }

    /**
     * 请求二级分类
     * lastId - last id，翻页使用
     * pcid - 一级分类id
     *
     */
    fun requestAnchorList(lastId: Int, categoryId: Int, callback: ArrayListCallback<AnchorItem>) {
        val paramMap: MutableMap<String, Any> = HashMap()
        paramMap["lid"] = lastId
        paramMap["pcid"] = categoryId
        paramMap["pi"] = 0
        requestWithToken(NetWork.CATEGORY, JSON.toJSONString(paramMap), object : NetworkCallback {
            override fun callback(item: BaseItem) {
                val dataString = item.data
                FileTool.writeFile("$dir/$categoryId/$categoryId.json", dataString)
                val dataJson = JSON.parseObject(dataString)
                anchorLastId = dataJson["lid"] as Int
                val contentString = JSON.toJSONString(dataJson["content"]) ?: return;
                val anchorList = JSON.parseArray(contentString, OriginAnchorItem::class.javaObjectType)
                val newAnchorList = arrayListOf<AnchorItem>()
                for (originAnchor: OriginAnchorItem in anchorList) {
                    requestPackageList(categoryId, originAnchor, object : ArrayListCallback<PackageItem> {
                        override fun callback(list: List<PackageItem>) {
                            val newAnchorItem = AnchorItem()
                            newAnchorItem.title = originAnchor.title
                            newAnchorItem.id = originAnchor.id
                            newAnchorItem.packageList = list
                            // TODO: 下载图片
                            newAnchorItem.iconURL = "${originAnchor.id}.jpg"
                            newAnchorList.add(newAnchorItem)


                            callback.callback(newAnchorList)
                        }
                    })
                    break
                }
            }
        })
    }



    fun requestPackageList(categoryId: Int, originAnchor: OriginAnchorItem, callback: ArrayListCallback<PackageItem>) {
        val anchorId = originAnchor.id
        val anchorName = originAnchor.title
        val paramMap: MutableMap<String, Any> = HashMap()
        paramMap["lid"] = packageLastId;
        paramMap["cid"] = anchorId;
        paramMap["pi"] = 0;
        NetWork.requestWithToken(NetWork.PACKAGE, JSON.toJSONString(paramMap), object : NetworkCallback {
            override fun callback(item: BaseItem) {
                val dataString = item.data
                val dataJson = JSON.parseObject(dataString)
                packageLastId = dataJson["lid"] as Int
                val contentString = JSON.toJSONString(dataJson["content"]) ?: return;
                val packageList = JSON.parseArray(contentString, OriginPackageItem::class.javaObjectType)
                val list = createPackageList(categoryId, anchorId, packageList)
                callback.callback(list)
            }
        })
    }

    fun createPackageList(categoryId: Int, anchorId: Int, list: List<OriginPackageItem>): List<PackageItem> {
        val path = "$dir/$categoryId/$anchorId"
        val packageItemsList = arrayListOf<PackageItem>()
        if (list.isEmpty())  return packageItemsList
        for (i: Int in 1..list.size) {
            val packageItem = list[i - 1]
            val packageTitle = packageItem.title
            val localUrl = "${categoryId}_${anchorId}_$i.mp3"
            val newItem = PackageItem()
            newItem.id = i
            newItem.title = packageTitle
            newItem.sourceUrl = localUrl
            newItem.playTime = packageItem.playTime
            newItem.fileSize = packageItem.fileSize
            packageItemsList.add(newItem)
            // TODO: 下载mp3
            FileTool.writeFile("$path/$localUrl", packageItem.sourceUrl)
        }
        return packageItemsList
    }

    interface ArrayListCallback<T> {
        fun callback(list: List<T>)
    }
}