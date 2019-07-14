var app = new Vue({
	el:"#app",
	data:{
        cartList: [],
        totalMoney:0,//总金额
        totalNum:0//总数量
	},
	methods:{
        findCartList: function () {
            axios.get('/cart/findCartList.shtml').then(function (response) {
                //获取购物车列表数据
                app.cartList = response.data;
                app.totalMoney=0;
                app.totalNum=0;
                let cartListAll = response.data;

                for(let i=0;i<cartListAll.length;i++){
                    let cart = cartListAll[i];
                    for(let j=0;j<cart.orderItemList.length;j++){
                        app.totalNum+=cart.orderItemList[j].num;
                        app.totalMoney+=cart.orderItemList[j].totalFee;
                    }
                }
            });
        },
        /**
         * 向已有的购物车添加商品
         * @param itemId
         * @param num
         */
        addGoodsToCartList:function (itemId,num) {
            axios.get('/cart/addGoodsToCartList.shtml', {
                params: {
                    itemId:itemId,
                    num:num
                }
            }).then(function (response) {
                if(response.data.success){
                    //添加成功
                    app.findCartList();
                }else{
                    //添加失败
                    alert(response.data.message);
                }
            });
        }

	},
	created:function(){
        this.findCartList();
	}
})
