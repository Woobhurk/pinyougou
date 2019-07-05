var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        searchEntity: {},
        resultMap: {brandList: [], categoryList: [], specList: []},
        searchMap: {'keywords': "", 'category': "", 'brand': "", spec: {},'price':''}
    },
    methods: {
        searchList: function () {
            axios.post('/itemSearch/search.shtml', this.searchMap).then(function (response) {
                app.resultMap = response.data;
                console.log(app.resultMap.specList)
                //默认获取第一个值
                console.log(response.data);
            });
        },
        addSearchItem: function (key, value) {
            //因为规格和规格选项都需要点击方法,可以在传入参数的时候加入一个key,
            //根据key的名字判断是哪个方法调用
            //获取到点击选项面板的值放入查询条件集合
            if (key == 'category' || key == 'brand'|| key=='price') {
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
            if (key == 'category' || key == 'brand'||key=='price') {

                this.searchMap[key] ='';
            } else {
                //如果这样写会报空指针 因为给里面的值赋空之后
                //后台规格过滤查询的时候,key是有的 但是值是空
                // this.searchMap.spec[key]='';
                //使用delete方法 js的方法 删除属性
                delete this.searchMap.spec[key];
            }

            //重新发送查询请求
            this.searchList();
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
        this.searchList();


    }

})
