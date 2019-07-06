var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        pageLabels: [],
        preDotted: false,
        nextDotted: false,
        entity: {},
        ids: [],
        searchEntity: {},
        resultMap: {brandList: [], categoryList: [], specList: []},
        searchMap: {'keywords': "", 'category': "", 'brand': "", spec: {}, 'price': '', 'pageNo': 1, 'pageSize': 40,'sortType':"",'sortField':""}
    },
    methods: {
        searchList: function () {
            axios.post('/itemSearch/search.shtml', this.searchMap).then(function (response) {
                app.resultMap = response.data;
                console.log(app.resultMap.specList);
                //查询后调用方法 不能写在axios外面 要不然总页数会为空
                app.buildPageLabel();
                //默认获取第一个值
                console.log(response.data);
            });
        },
        addSearchItem: function (key, value) {
            //因为规格和规格选项都需要点击方法,可以在传入参数的时候加入一个key,
            //根据key的名字判断是哪个方法调用
            //获取到点击选项面板的值放入查询条件集合
            if (key == 'category' || key == 'brand' || key == 'price') {
                this.searchMap[key] = value;
            } else {
                this.searchMap.spec[key] = value;
            }
            // 发送请求执行搜索
            this.searchList();
        },
        //移除面包屑导航的搜索项
        removeSearchItem: function (key) {
            //1.移除变量里面的值
            if (key == 'category' || key == 'brand' || key == 'price') {

                this.searchMap[key] = '';
            } else {
                //如果这样写会报空指针 因为给里面的值赋空之后
                //后台规格过滤查询的时候,key是有的 但是值是空
                // this.searchMap.spec[key]='';
                //使用delete方法 js的方法 删除属性
                delete this.searchMap.spec[key];
            }

            //重新发送查询请求
            this.searchList();
        },
        queryByPage: function (page) {
            //传入的页码为文本 需要把传入的页码转为int类型的值
            //否则会以字符串传入后台 报出类型转换错误
            var number = parseInt(page);
            //限制输入的页码
            if (number > this.resultMap.totalPages) {
                number = this.resultMap.totalPages;
            }
            if (number < 1) {
                number = 1;
            }
            this.searchMap.pageNo = number;
            this.searchList();
        }
        ,
        //分页控件
        buildPageLabel: function () {
            //如果不清空 每次点击的时候会有一个往里面在添加一次12345的bug
            this.pageLabels = [];
            //要回复初始值
            this.preDotted = false;
            this.nextDotted = false;
            //显示以当前页为中心的五个页码 定义初始值
            //当前页
            let firstPage = 1;
            //总页数
            let lastPage = this.resultMap.totalPages;

            if (this.resultMap.totalPages > 5) {
                //判断如果当前页码小于等于3  显示前五页数据
                if (this.searchMap.pageNo <= 3) {
                    firstPage = 1;
                    lastPage = 5;
                    this.preDotted = false;
                    this.nextDotted = true;

                    //当前页 大于等于 总页数-2 显示后5页数据 要不然会超出
                } else if (this.searchMap.pageNo >= this.resultMap.totalPages - 2) {
                    firstPage = this.resultMap.totalPages - 4;
                    lastPage = this.resultMap.totalPages;
                    this.preDotted = true;
                    this.nextDotted = false;
                } else {
                    //总页数小于5
                    firstPage = this.searchMap.pageNo - 2;
                    lastPage = this.searchMap.pageNo + 2;
                    this.preDotted = true;
                    this.nextDotted = true;
                }

            } else {
                //如果总页数小于5页
                firstPage = 1
                lastPage = this.resultMap.totalPages;
                this.preDotted = false;
                this.nextDotted = false;
            }

            //页码要从1开始 所以小于等于
            for (var i = firstPage; i <= lastPage; i++) {
                //往数组添加页码
                this.pageLabels.push(i)
            }
        },
        clear: function () {
            this.searchMap={'keywords':this.searchMap.keywords,'category':'','brand':'',spec:{},'price':'','pageNo':1,'pageSize':40,'sortType':"",'sortField':""};
        },
        doSort:function (sortType,sortField) {
            //把传递过来的参数赋值给变量
            this.searchMap.sortType = sortType;
            this.searchMap.sortField = sortField;
            this.searchList();
        },
        isKeywordsIsBrand:function(){
            //判断搜索的关键字是否就是品牌,如果是返回true 否则返回false
            //非空判断
            if (this.resultMap.brandList != null && this.resultMap.brandList.length > 0) {
                //循环品牌列表 判断搜索关键字是否包含品牌列表中存在 如果是返回true
                for (var i = 0; i < this.resultMap.brandList.length; i++) {
                    //只要搜索关键字包含了品牌就要隐藏
                    //通过字符串找下标的方式判断集合属性
                    //this.resultMap.brandList[i].text所有的 品牌(三星,华为) !=-1就是找到了
                    if (this.searchMap.keywords.indexOf(this.resultMap.brandList[i].text) != -1) {
                        //找到了就给变量赋值,面包屑导航就有值了
                        this.searchMap.brand=this.resultMap.brandList[i].text;
                        return true;
                    }
                }
                return false;
            }



        }




    },

    //钩子函数 初始化了事件和
    created: function () {
        //解码拿到首页传过来的搜索关键字
        var urlParamObj = this.getUrlParam();
        //非空判断
        if (urlParamObj.keywords != undefined && urlParamObj != null) {
            this.searchMap.keywords=decodeURIComponent(urlParamObj.keywords);
            this.searchList();
        }


        this.searchList();


    }

})
