var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        //对应实体类的属性
        entity: {
            goods: {},
            goodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []},
            itemList: []
        },
        ids: [],
        itemCat1List: [], // 一级分类的列表
        itemCat2List: [], // 一级分类的列表
        itemCat3List: [],//三级分类的列表
        searchEntity: {},
        brandTextList: [],
        image_entity: {url: '', color: ''},
        customA: [],
        specList: [],//模板关联的规格选项集合

    },
    methods: {
        searchList: function (curPage) {
            axios.post('/goods/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
                //获取数据
                app.list = response.data.list;

                //当前页
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;


            });
        },
        //查询所有品牌列表
        findAll: function () {
            console.log(app);
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/goods/findPage.shtml', {
                params: {
                    pageNo: this.pageNo
                }
            }).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data.list;
                app.pageNo = curPage;
                //总页数
                app.pages = response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add: function () {
            //添加商品(3个表)
            //通过kindEditer的方法 获取html代码 赋值给变量中 商品描述的属性
            this.entity.goodsDesc.introduction = editor.html();
            axios.post('/goods/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    //添加成功后清除页面属性值
                    //变量中对应实体类的属性
                    app.entity = {goods: {}, goodsDesc: {}, itemList: []}
                    alert("保存成功")
                    window.location.reload();
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/goods/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save: function () {
            if (this.entity.id != null) {
                this.update();
            } else {
                this.add();
            }
        },
        findOne: function (id) {
            axios.get('/goods/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/goods/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        goListPage: function () {
            window.location.href = "goods.html"
        },
        upload: function () {
            //创建一个表单对象 用于生成一个虚拟表单
            var formData = new FormData();

            //参数formData.append('file' 中的file 为表单的参数名(name)  必须和 后台的file一致  (<input type="file" name="file" value="文件本身" />)

            //file.files[0]  中的file 指定的时候页面中的input="file"的id的值 files 指定的是选中的图片所在的文件对象数组，这里只有一个就选中[0]
            //
            formData.append('file', file.files[0]);
            axios({
                url: 'http://localhost:9110/upload/uploadFile.shtml',
                data: formData,
                method: 'post',
                //设置表单提交的数据类型
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                //开启跨域请求携带相关认证信息
                withCredentials: true
            }).then(function (response) {
                if (response.data.success) {
                    //上传成功
                    console.log(response.data.message);
                    //如果上传成功就把url赋值给需要展示的缩略图标签
                    app.image_entity.url = response.data.message;
                    console.log(JSON.stringify(app.image_entity));
                } else {
                    //上传失败
                    alert(response.data.message);
                }
            })
        },
        addImageEntity: function () {
            // 每添加一张图片就向数组中添加一个对象
            this.entity.goodsDesc.itemImages.push(this.image_entity);

        },
        removeImage: function (index) {
            //移除图片
            this.entity.goodsDesc.itemImages.splice(index, 1);
        },
        //获取一级分类的类别方法名 一级分类为0就不用传参数 可以写死 0
        findCat1List: function () {
            axios.get('/itemCat/findByParentId/0.shtml').then(function (response) {
                //获取列表数据, 使用一个变量绑定

                app.itemCat1List = response.data;
            }).catch(function (error) {
                console.log("12121212121")
            })
        },
        //当点击复选框的时候调用 并影响变量 entity.goodsDesc.specficationItems的值
        /**
         *
         * @param specName 网络
         * @param specValue 移动4G..
         */
        updateChecked: function ($event, specName, specValue) {
            var obj = this.searchObjectByKey(this.entity.goodsDesc.specificationItems, 'attributeName', specName);
            //1.如果变量中已经有对象了 先获取该对象, 设置它的attributeValue的值
            if (obj != null) {
                // 要解决勾选和取消勾选的问题 所以这里需要加上一个判断
                // 使用 $event 事件判断 是否选中
                //$event.target.checked 获取选中状态
                if ($event.target.checked) {
                    //如果是勾选 添加数据
                    //向数组属性中添加规格的选项值
                    obj.attributeValue.push(specValue)
                } else {
                    //如果是取消 使用splice方法删除数据
                    //  obj.attributeValue.indexOf(specValue) 从obj.attributeValue 数组中 去找到 值为 specValue 的下标
                    obj.attributeValue.splice(obj.attributeValue.indexOf(specValue), 1);
                    //如果对象中没有值 存入数据库是没有意义的所以 把对象删除

                    if (obj.attributeValue.length == 0) {
                        //使用splice 方法删除
                        this.entity.goodsDesc.specificationItems.splice(
                            //obj为空了 就直接找到obj的下标 删除obj就可以了
                            this.entity.goodsDesc.specificationItems.indexOf(obj), 1
                        );
                    }
                }
            } else {
                //2.如果变量中没有对象 直接添加一个对象
                this.entity.goodsDesc.specificationItems.push(
                    {"attributeValue": [specValue], "attributeName": specName}
                )
            }
        },
        /**
         *
         * @param list  要搜索的对象数组 这里使用list是为了方法的复用,调用者传入什么类型都可以
         * @param specName 要找的属性的名称  传入 为了方法的复用
         * @param key  要找的属性名称 对应的值
         * @returns {*}
         */
        //从specificationItems 变量中 根据规格的名称来查询 是否有对象
        searchObjectByKey: function (list, specName, key) {
            /*
               {"attributeValue":["移动3G","移动4G"],"attributeName":"网络"}..
            */
            var specItems = list;
            //遍历集合 取出里面的name判断是否存在
            for (var i = 0; i < specItems.length; i++) {
                var obj = specItems[i];
                // 这里写 obj[specName] 相当于是取attributeName里面的值
                if (key == obj[specName]) {
                    return obj;
                }
            }
            //如果不包含数据 返回一个空即可
            return null;
        },
        //每次点击复选框的时候调用
        createList: function () {
            //1.定义初始化值
            this.entity.itemList = [{"spec": {}, "price": 0, "num": 0, "status": "0", "isDefault": "0"}];
            //2.循环遍历  从specificationItems
            var specificationItems = this.entity.goodsDesc.specificationItems;
            for (var i = 0; i < specificationItems.length; i++) {
                //3.获取规格名称和规格选项的值 拼接 返回一个最新的 SKU列表
                var obj = specificationItems[i];
                //拼接完成后的列表赋值给SKU列表
                this.entity.itemList = this.addColumn(
                    this.entity.itemList,
                    obj.attributeName,
                    obj.attributeValue);


            }

        },
        /**
         * 获取规格名称和规格选项的值 拼接 返回一个最新的 SKU列表
         * @param list SKU列表
         * @param columnName 网 络
         * @param columnValue  移动3G..
         */
        addColumn: function (list, columnName, columnValue) {
            var newList = [];
            for (var i = 0; i < list.length; i++) {
                var oldRow = list[i]; //  {'spec':{},'price':0,'num':'0','isDefault':'0'}

                for (var j = 0; j < columnValue.length; j++) {
                    var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                    var value = columnValue[j]; //移动3G 拼接到spec
                    // 赋给columnName里面的值
                    newRow.spec[columnName] = value;
                    newList.push(newRow)
                }
            }
            return newList;
        }


    },
    //定义一个监听
    watch: {
        //监听一个变量:entity.goods.category1Id的变化 根据点击不同的id找到下面的子级
        //第一个参数是新值 就是现在当前选中的值
        //第二个参数是旧值,就是上一次选中时的值
        'entity.goods.category1Id': function (newval, oldval) {
            //进行一个判断 因为有可能还没有新值.
            if (newval != undefined) {
                //根据新选中值的id发送一个请求查询它下面所有的子级分类
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(function (response) {
                    //获取列表数据, 使用一个变量绑定
                    app.itemCat2List = response.data;
                }).catch(function (error) {
                    console.log("12121212121")
                })
            }
        },
        //监听二级分类
        'entity.goods.category2Id': function (newval, oldval) {
            // 进行非空判断
            if (newval != undefined) {
                axios.get('/itemCat/findByParentId/' + newval + '.shtml').then(function (response) {
                    app.itemCat3List = response.data;
                }).catch(function (error) {
                    console.log("121212121212")
                })
            }
        },
        //监听三级分类 动态查询模板id
        'entity.goods.category3Id': function (newval, oldval) {
            // 进行非空判断
            if (newval != undefined) {
                axios.get('/itemCat/findOne/' + newval + '.shtml').then(function (response) {
                    //获取三级列表模板信息
                    //    app.entity.goods.typeTemplateId=response.data.typeId;
                    app.$set(app.entity.goods, 'typeTemplateId', response.data.typeId);
                }).catch(function (error) {
                    console.log("121212121212")
                })
            }
        },
        //监听模板的变化  查询该模板的对象 对象里面有个品牌列表数据
        'entity.goods.typeTemplateId': function (newval, oldval) {
            // 进行非空判断
            if (newval != undefined) {
                axios.get('/typeTemplate/findOne/' + newval + '.shtml').then(function (response) {
                    //取出模板对象
                    var typeTemplate = response.data;
                    //因为数据取出来是一个json的字符串 需要转换成json对象页面才能遍历
                    app.brandTextList = JSON.parse(typeTemplate.brandIds);//传回来的数据格式 是 "id":1,"text":"联想"
                    //获取模板对象中扩展属性的值,定义一个变量接收
                    app.entity.goodsDesc.customAttributeItems = JSON.parse(typeTemplate.customAttributeItems);
                })

                //监听模板的变化 根据模板的id 获取模板规格的数据拼接成想要的格式
                axios.get('/typeTemplate/findSpecList/' + newval + '.shtml').then(function (response) {
                    app.specList = response.data;


                })

            }
        }


    }


    ,
    //钩子函数 初始化了事件和
    created: function () {

        this.findCat1List();

    }

})
