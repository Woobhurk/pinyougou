var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        resultMap:{},
        searchMap:{keywords:"手机"}
    },
    methods: {
        searchList:function () {
            axios.post('/itemSearch/search.shtml',this.searchMap).then(function (response) {
                //获取结果集
                app.resultMap = response.data;
                //默认获取第一个值
                console.log(response.data);
            });
        }


    },
    //钩子函数 初始化了事件和
    created: function () {
      this.searchList();


    }

})
