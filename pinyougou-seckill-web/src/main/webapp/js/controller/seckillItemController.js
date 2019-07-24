var app = new Vue({
    el: "#app",
    data: {
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        count1:'',//库存
        seckillId:0,
        timeString:'',
        messageInfo:'',
        searchEntity: {}
    },
    methods:{
        //当点击立即抢购的时候调用
        submitOrder:function(){
          axios.get('/seckillOrder/submitOrder/'+this.seckillId+'.shtml').then(
              function (response) {
                  if (response.data.success) {
                      //跳转到支付页面
                      app.messageInfo=response.data.message;
                  }else {
                      if (response.data.message=='403'){
                          alert("需要登陆")
                          var url = window.location.href; //获取当前浏览器中的url
                          window.location.href = "http://localhost:9109/page/login.shtml?url=" + url;
                      }
                      alert(response.data.message)
                  }
              }
          )
        },
        /**
         *
         * @param alltime 为 时间的毫秒数。
         * @returns {string}
         */
        convertTimeString:function(alltime){
            var allsecond=Math.floor(alltime/1000);//毫秒数转成 秒数。
            var days= Math.floor( allsecond/(60*60*24));//天数
            var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小数数
            var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
            var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数

            if(days>0&&days<10){
                days="0"+days+"天 ";
            }
            if(days>10){
                days=days+"天 ";
            }
            if(hours<10){
                hours="0"+hours;
            }
            if(minutes<10){
                minutes="0"+minutes;
            }
            if(seconds<10){
                seconds="0"+seconds;
            }
            return days+hours+":"+minutes+":"+seconds;
        },
        //根据商品的ID 获取商品的数据：剩余时间的毫秒数以及 商品的库存
        getGoodsById: function () {
            axios.get('/seckillGoods/getGoodsById.shtml?id='+this.seckillId).then(
                function (response) {

                console.log(response.data);
                app.caculate(response.data.time) //参数是毫秒
                app.count1 = response.data.count; //剩余库存
                console.log(app.count);
            })
        },
        //倒计时
        caculate: function (alltime) {
            let clock = window.setInterval(function () {
                alltime = alltime - 1000;
                //反复被执行的函数
                app.timeString = app.convertTimeString(alltime);
                //跳出循环条件倒计时到0结束
                if (alltime <= 0) {
                    //取消
                    window.clearInterval(clock);
                }
            }, 1000);//相隔1000执行一次。
        },
        //因为是异步 所以页面要主动查询查询订单的状态给用户反馈  点击立即抢购后立即执行,
        queryStatus:function () {
            let count =0;
            //三秒钟执行一次
            let queryorder  = window.setInterval(function () {
                count+=3;
                axios.get('/seckillOrder/queryOrderStatus.shtml').then(function (response) {
                    console.log("正在查询.............状态值"+response.data.message);
                    if (response.data.success) {
                        //跳转到支付页面
                        window.location.href ="pay/pay.html"
                        //停止方法 直接跳转不需要停止
                        // window.clearInterval(queryorder)
                    } else {
                        if (response.data.message == '403') {
                            //要登录
                        } else {
                            //不需要登录需要提示
                            app.messageInfo = response.data.message + "....." + count;


                        }
                    }
                })
            },3000)
        }
    },
    created:function () {
        //开始执行 获取参数
        var urlParam = this.getUrlParam("id");
        //获取秒杀商id
        this.seckillId = urlParam.id;
        this.getGoodsById()

    }
});