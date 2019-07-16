var app = new Vue({
	el:"#app",
	data:{
        cartList: [],
        totalMoney:0,//总金额
        totalNum:0,//总数量
        addressList:[],
        address:{},
        order:{paymentType:'1'},
        entity: {tbAddress:{}},
        tbProvincesList: [], // 省列表
        tbCitiesList: [], // 市列表
        tbAreasList: [] // 区列表
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
        },
        findAddressList:function () {
            axios.get('/address/findAddressListByUserId.shtml').then(function (response) {
                app.addressList=response.data;
            })
        },
        selectAddress:function (address) {
            this.address=address;
        },
        isSelectedAddress:function (address) {
            if (address == this.address) {
                return true;
            }
            return false;
        },
        //获取省分类的方法
        findProvincesList:function () {
            axios.get('/address/findProvincesList.shtml').then(function (response) {
                app.tbProvincesList=response.data;
            })
        },
        //新增收货地址的方法
        add:function () {
            axios.post('/address/add.shtml', this.entity.tbAddress).then(function (response) {
                console.log(response);
                if (response.data.success) {
                    //添加成功后清除页面属性值
                    //变量中对应实体类的属性
                    app.entity = {tbAddress: {}}
                    alert("保存成功")
                    window.location.reload();
                }
            }).catch(function (error) {
                console.log("添加失败");
            })

        },
        selectType:function (type) {
            console.log(type);
            this.$set(this.order,'paymentType',type);
            //this.order.paymentType=type;
        },
        //添加一个方法
        submitOrder: function () {
            //获取用户输入的值设置传入后台,要不然后台没数据
            this.$set(this.order,'receiverAreaName',this.address.address);
            this.$set(this.order,'receiverMobile',this.address.mobile);
            this.$set(this.order,'receiver',this.address.contact);
            axios.post('/order/add.shtml', this.order).then(
                function (response) {
                    if(response.data.success){
                        //跳转到支付页面
                        window.location.href="pay.html";
                    }else{
                        alert(response.data.message);
                    }
                }
            )
        }

	},
    //监听
    watch:{
	    //监听省份
    'entity.tbAddress.provinceId':function (newval, oldval) {
        //赋值为空
        this.tbAreasList = [];
        //删除属性回到原始状态
        if (this.entity.tbAddress.provinceId == null) {
            delete this.entity.tbAddress.cityId;

            delete this.entity.tbAddress.townId;
        }
        //进行一个判断 因为有可能还没有新值.
        if (newval != undefined) {
            //根据新选中值的id发送一个请求查询它下面所有的子级分类
            axios.get('/address/findByCityId/' + newval + '.shtml').then(function (response) {
                //获取列表数据, 使用一个变量绑定
                app.tbCitiesList = response.data;
            }).catch(function (error) {
                console.log("12121212121")
            })
        }
    },
        //监听城市
        'entity.tbAddress.cityId': function (newval, oldval) {
            // 进行非空判断
            if (newval != undefined) {
                axios.get('/address/findByAreasId/' + newval + '.shtml').then(function (response) {
                      app.tbAreasList=response.data;
                   // app.$set(app.entity.goods, 'typeTemplateId', response.data.typeId);
                }).catch(function (error) {
                    console.log("121212121212")
                })
            }
        }












    }








    ,
	created:function(){
        this.findCartList();
        if (window.location.href.indexOf("getOrderInfo.html") != -1) {
            this.findAddressList();
        }
        this.findProvincesList();
	}
})
