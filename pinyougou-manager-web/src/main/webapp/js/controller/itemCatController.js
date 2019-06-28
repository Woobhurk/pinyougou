var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {parentId: 0},
        ids: [],
        searchEntity: {},
        entity_1: {},
        entity_2: {},
        grade: 1,
        parent_id:""


    },
    methods: {
        aaa:function(){

            app.entity={}
        },
        searchList: function (curPage) {
            axios.post('/itemCat/search.shtml?pageNo=' + curPage, this.searchEntity).then(function (response) {
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
            axios.get('/itemCat/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list = response.data;

            }).catch(function (error) {

            })
        },
        findPage: function () {
            var that = this;
            axios.get('/itemCat/findPage.shtml', {
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
            console.log(app.entity)
            axios.post('/itemCat/add.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.grade=1;
                    app.selectList({id:0});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update: function () {
            axios.post('/itemCat/update.shtml', this.entity).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.grade=1;
                    app.selectList({id:0});

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
            axios.get('/itemCat/findOne/' + id + '.shtml').then(function (response) {
                app.entity = response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele: function () {
            axios.post('/itemCat/delete.shtml', this.ids).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    app.grade=1;
                    app.selectList({id:0});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        findByParentId: function (parentId) {

            axios.get('/itemCat/findByParentId/' + parentId + '.shtml').then(function (response) {
            app.list = response.data;
           //单独定义一个变量用来接收上级id
            app.entity.parentId=parentId;
                //查询的时候记录上级ID 赋值给全局变量
            app.parent_id = parentId

            console.log(app.entity.parentId)

            }).catch(function (error) {
                console.log("111111111111")
            })
        },
        //参数就是 被点击的那个对象
        selectList: function (p_entity) {
            //如果等级是1就不显示下面的层级
            if (this.grade == 1) {
                this.entity_1 = {};
                this.entity_2 = {};
            }
            //如果等级是2显示第二级不显示第三级
            if (this.grade == 2) {
                this.entity_1 = p_entity;
                this.entity_2 = {};
            }
            //如果等级是3显示第三级 第二级不变
            if (this.grade == 3) {
                //entity1的值不变
                this.entity_2 = p_entity;
            }
            //因为不能给一个按钮绑定多个点击事件,所以在这里调用查下一层级的方法,在页面只绑定这一个方法即可
            this.findByParentId(p_entity.id);
        },
        locationReload:function () {
            window.location.reload();
        }

    },
    //钩子函数 初始化了事件和
    created: function () {
        //在初始化的时候使用钩子函数调用声明一个初始参数
        this.selectList({id:0});



    }
})
