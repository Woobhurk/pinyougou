var app = new Vue({
    el: "#app",
    data: {
        entity:{}
    },
    methods: {
        register:function () {
            axios.post('/seller/add.shtml',this.entity).then(function (response) {
                //判断传递过来的状态
                if(response.data.success){
                    location.href='/shoplogin.html';
                }else {
                    alert(response.message)
                }
            })

        }





    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

    }

})
