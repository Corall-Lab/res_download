package com.example.lib

import com.example.lib.NetWork.requestWithToken
import kotlin.jvm.JvmStatic
import com.alibaba.fastjson.JSON
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.net.URI
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
                            newAnchorItem.id = "$categoryId${originAnchor.id}".toInt()
                            newAnchorItem.packageList = list
                            val fileName = "$categoryId${originAnchor.id}.${URL(originAnchor.iconURL).file.split(".").last()}"
                            // 下载图片
                            GlobalScope.launch(Dispatchers.IO) {
                                val connect = URL(originAnchor.iconURL).openConnection()
                                connect.connect()
                                connect.getInputStream().use { input ->
                                    BufferedOutputStream(FileOutputStream("$dir/$categoryId/$fileName")).use { output ->
                                        input.copyTo(output) //将文件复制到本地 其中copyTo使用方法可参考我的Io流笔记
                                    }
                                }
                            }
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
            val fileName = "${categoryId}${anchorId}$i.${URL(packageItem.sourceUrl).file.split(".").last()}"
            val newItem = PackageItem()
            newItem.id = "${categoryId}${anchorId}$i".toInt()
            newItem.title = packageTitle
            newItem.playTime = packageItem.playTime
            newItem.fileSize = packageItem.fileSize
            packageItemsList.add(newItem)
            // 下载mp3
            GlobalScope.launch(Dispatchers.IO) {
                print("===== download ${path}/${fileName}\n")
                FileTool.createOrExistDir(path)
                val connect = URL(packageItem.sourceUrl).openConnection()
                connect.connect()
                connect.getInputStream().use { input ->
                    BufferedOutputStream(FileOutputStream("$path/$fileName")).use { output ->
                        input.copyTo(output)
                    }
                }
            }

//            FileTool.writeFile("$path/$fileName", packageItem.sourceUrl)
        }
        return packageItemsList
    }

    interface ArrayListCallback<T> {
        fun callback(list: List<T>)
    }
}