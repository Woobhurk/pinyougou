var app = new Vue({
	el:"#app",
	data:{
        totalMoney:0,
        payObject:{}//封装支付的金额 二维码连接 交易订单号



	},
	methods:{
        createNative:function () {
            var that=this;
            axios.get('/pay/createNative.shtml').then(
                function (response) {
                    //如果有数据
                    if(response.data){
                        app.payObject=response.data;
                        //把分转换成元
                        app.payObject.total_fee=app.payObject.total_fee/100;
                        //生成二维码
                        var qr = new QRious({
                            element:document.getElementById('qrious'),
                            size:250,
                            level:'H',
                            value:app.payObject.code_url
                        });
                        //已经生成二维码后
                        if (qr) {
                            //发送请求获取值
                            app.queryPayStatus(app.payObject.out_trade_no);
                        }
                    }
                }
            )
        },
        //根据支付的订单号发送请求查询支付状态
        queryPayStatus:function (out_trade_no) {
            axios.get('/pay/queryPayStatus.shtml',{
                params:{
                    out_trade_no:out_trade_no
                }
            }).then(function (response) {
                if(response.data){
                    if(response.data.success){
                        //支付成功
                        alert("1111111111111111111111111111111")
                        window.location.href="paysuccess.html?money="+app.payObject.total_fee;
                    }else  {
                        //支付失败
                        if(response.data.message=='支付超时'){
                            //在调用一次生成二维码的方式 刷新二维码
                            alert("支付超时重新生成二维码");
                            app.createNative();//刷新二维码
                        }else{
                            window.location.href="payfail.html";
                        }
                    }
                }else{
                    alert("错误");
                }
            })
        }
	},
	created:function(){
        //页面一加载就应当调用
        if(window.location.href.indexOf("pay.html")!=-1){
            this.createNative();
        }else {
            let urlParamObject = this.getUrlParam();
            if(urlParamObject.money)
                alert(urlParamObject.money)
                this.payObject.total_fee=urlParamObject.money;
        }
    }



})
