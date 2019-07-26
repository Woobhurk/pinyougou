var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        ids:[],
        status:['未审核','已审核','审核未通过','已关闭'],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/specification/searchByOption.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
              //  console.log(response.data.list)
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        updateStatus:function (status) {
            axios.post('/specification/updateStatus/'+status+'.shtml',this.ids).then(function (response) {
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

    }

});
