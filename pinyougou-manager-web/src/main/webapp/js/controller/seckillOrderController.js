var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        status:['未支付','已支付'],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/seckillOrder/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;

                var Array= app.list;

                for(var i = 0; i < Array.length; i++){
                    var obj = app.formatDate(Array[i].createTime)
                    var obj1 = app.formatDate(Array[i].payTime)

                    Array[i].createTime = obj
                    Array[i].payTime = obj1
                }
                app.list =  Array;

            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/seckillOrder/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/seckillOrder/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/seckillOrder/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/seckillOrder/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/seckillOrder/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/seckillOrder/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        formatDate: function (value) {// 时间戳转换日期格式方法
            if (value == null) {
                return '';
            } else {
                var date = new Date(value);
                var y = date.getFullYear();// 年
                var MM = date.getMonth() + 1;// 月
                MM = MM < 10 ? ('0' + MM) : MM;
                var d = date.getDate();// 日
                d = d < 10 ? ('0' + d) : d;
                var h = date.getHours();// 时
                h = h < 10 ? ('0' + h) : h;
                var m = date.getMinutes();// 分
                m = m < 10 ? ('0' + m) : m;
                var s = date.getSeconds();// 秒
                s = s < 10 ? ('0' + s) : s;
                return y + '-' + MM + '-' + d + ' ' + h + ':' + m + ':' + s;

            }
        },



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

    }

})
