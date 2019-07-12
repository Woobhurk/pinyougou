var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        smsCode: '',
        ids: [],
        searchEntity: {}
    },
    methods: {
        //注册
        register: function () {
            axios.post('/user/add/' + this.smsCode + '.shtml', this.entity).then(function (response) {
                if (response.data.success) {
                    //跳转到登录页面
                    window.location.href = "home-index.html";
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //点击a标签的时候 调用方法 发送请求 发送验证码
        createSmsCode: function () {
            axios.get('/user/sendCode.shtml?phone=' + this.entity.phone).then(
                function (response) {//result
                    alert(response.data.message);
                }
            )
        },

        formSubmit: function () {
            var that = this;
            this.$validator.validate().then(
                function (result) {
                    if (result) {
                        console.log(that);
                        axios.post('/user/add/' + that.smsCode + '.shtml', that.entity).then(function (response) {
                            if (response.data.success) {
                                //跳转到其用户后台的首页
                                window.location.href = "home-index.html";
                            } else {
                                that.$validator.errors.add(response.data.errorsList);
                            }
                        }).catch(function (error) {
                            console.log("1231312131321");
                        });
                    }
                }
            )
        },
        //钩子函数 初始化了事件和
        created: function () {


        }

    }
})
