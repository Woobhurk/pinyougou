﻿var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {customAttributeItems: []}, //初始化
        brandOptions: [],//显示品牌的列表
        specOptions: [],//显示规格的列表
        status:['未审核','已审核','审核未通过','已关闭'],
        ids: [],
        searchEntity: {}
    },
    methods: {
        searchList: function (curPage) {
            axios.post('/typeTemplate/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
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
            axios.get('/typeTemplate/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/typeTemplate/findPage.shtml', {
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
            axios.post('/typeTemplate/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/typeTemplate/update.shtml', this.entity).then(function (response) {
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
            axios.get('/typeTemplate/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
                app.entity.brandIds=JSON.parse(app.entity.brandIds);
                app.entity.customAttributeItems = JSON.parse(app.entity.customAttributeItems);
                app.entity.specIds = JSON.parse(app.entity.specIds);
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/typeTemplate/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findBrandIds: function () {
            axios.get('/brand/findAll.shtml').then(function (response) {
                //从商品表中查出所有
                let brandList = response.data;//i[{id,name}]
                for (var i = 0; i < brandList.length; i++) {
                    //遍历出集合的中的属性 赋值给 brandOptions 向数组中添加对象 push方法
                    app.brandOptions.push({id: brandList[i], text: brandList[i].name});
                }

            }).catch(function (error) {
                console.log("123123123123");
            })

        },
        findSpecList:function () {
            axios.get('/specification/findAll.shtml').then(function (response) {
                //从规格表中查出所有 遍历赋值
                let specList = response.data;
                for (var i = 0; i < specList.length; i++) {
                    //遍历出集合的中的属性 赋值给 brandOptions 向数组中添加对象 push方法
                    app.specOptions.push({id: specList[i], text: specList[i].specName});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            })
            
        },
        addTableRow:function () {
            //点击一下新增就给出一个空的一栏
            this.entity.customAttributeItems.push({})

        },
        removeTableRow:function (index) {
            this.entity.customAttributeItems.splice(index, 1);
        },
        jsonToString:function (list, key) {
            //用于循环遍历 获取对象中的属性值 拼接字符串 返回
            var listJson = JSON.parse(list);
            var str = "";
            for (var i = 0; i < listJson.length; i++) {
                 var obj = listJson[i];
                str += obj[key] + ",";
            }
            if ( str.length>0 ){
                str = str.substring(0, str.length - 1);
            }
            return str;
        },
        updateStatus:function (status) {
            axios.post('/typeTemplate/updateStatus/'+status+'.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                    app.ids=[];
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

    },
    //钩子函数 初始化了事件和
    created: function () {

        this.searchList(1);

        this.findBrandIds();

        this.findSpecList();

    }

})
