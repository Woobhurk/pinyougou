window.onload = () => {
    new OrderQueryVm().main();
};

class OrderQueryVm {
    toolPanel;
    dataPanel;
    summaryPanel;
    pagePanel;

    main() {
        this.initVc();
        this.loadCategory1List();
        this.loadOrders();
    }

    initVc() {
        let _this = this;

        this.toolPanel = new Vue({
            el: '#toolPanel',
            data: {
                selectedCategory1: null,
                selectedCategory2: null,
                selectedCategory3: null,
                selectedTime: [],
                category1List: [],
                category2List: [],
                category3List: [],
                orderParam: {}
            },
            methods: {
                onChangeCategory1: function () {
                    _this.changeCategory1();
                },
                onChangeCategory2: function () {
                    _this.changeCategory2();
                },
                onChangeCategory3: function () {
                    _this.changeCategory3();
                },
                onChangeTime: function () {
                    _this.changeTime();
                },
                onSearchOrders: function () {
                    _this.loadOrders();
                },
                onClearParam: function () {
                    _this.clearParam();
                }
            }
        });

        this.dataPanel = new Vue({
            el: '#dataPanel',
            data: {
                ORDER_PAYMENT_TYPE: ['微信付款', '货到付款'],
                ORDER_STATUS: ['未付款', '已付款', '其他'],
                orderResultList: []
            },
            methods: {

            }
        });

        this.summaryPanel = new Vue({
            el: '#summaryPanel',
            data: {
                totalOrder: 0,
                totalPrice: 0.0
            },
            methods: {

            }
        });

        this.pagePanel = new Vue({
            el: '#pagePanel',
            data: {
                pageNum: 1,
                pageSize: 10,
                total: 0
            },
            methods: {
                onChangePage: function () {
                    _this.changePage();
                }
            }
        });
    }

    loadCategory1List() {
        AjaxUtils.post(`${SELLER_GOODS_WEB}/itemCat/findByParentId/0.shtml`,
            null, resultInfo => {
                this.toolPanel.category1List = resultInfo;
                this.toolPanel.category2List = [];
                this.toolPanel.category3List = [];
                this.toolPanel.selectedCategory1 = null;
                this.toolPanel.selectedCategory2 = null;
                this.toolPanel.selectedCategory3 = null;
            });
    }

    changeCategory1() {
        let parentId = this.toolPanel.selectedCategory1;

        AjaxUtils.post(`${SELLER_GOODS_WEB}/itemCat/findByParentId/${parentId}.shtml`,
            null, resultInfo => {
                this.toolPanel.category2List = resultInfo;
                this.toolPanel.category3List = [];
                this.toolPanel.selectedCategory2 = null;
                this.toolPanel.selectedCategory3 = null;
            });
    }

    changeCategory2() {
        let parentId = this.toolPanel.selectedCategory2;

        AjaxUtils.post(`${SELLER_GOODS_WEB}/itemCat/findByParentId/${parentId}.shtml`,
            null, resultInfo => {
                this.toolPanel.category3List = resultInfo;
                this.toolPanel.selectedCategory3 = null;
            });
    }

    changeCategory3() {
        this.toolPanel.orderParam.categoryId = this.toolPanel.selectedCategory3;
        this.loadOrders();
    }

    changeTime() {
        let startTime = this.toolPanel.selectedTime[0];
        let endTime = this.toolPanel.selectedTime[1];

        this.toolPanel.orderParam.startTime = startTime;
        this.toolPanel.orderParam.endTime = endTime;
        this.loadOrders();
    }

    loadOrders() {
        let pageNum = this.pagePanel.pageNum;
        let pageSize = this.pagePanel.pageSize;
        let orderParam = this.toolPanel.orderParam;

        AjaxUtils.post(`${SELLER_GOODS_WEB}/order/findByPageParam/${pageNum}/${pageSize}.shtml`,
            orderParam, resultInfo => {
                this.loadOrdersFinished(resultInfo);
            });
    }

    loadOrdersFinished(resultInfo) {
        let orderResultList = ObjectUtils.cloneObj(resultInfo.data.dataList);
        let totalPrice = 0.0;

        for (let orderResult of orderResultList) {
            totalPrice += orderResult.order.payment;
        }

        this.dataPanel.orderResultList = orderResultList;
        this.pagePanel.total = resultInfo.data.total;
        this.summaryPanel.totalOrder = this.pagePanel.total;
        this.summaryPanel.totalPrice = totalPrice;
    }

    clearParam() {
        this.toolPanel.category2List = [];
        this.toolPanel.category3List = [];
        this.toolPanel.selectedCategory1 = null;
        this.toolPanel.selectedCategory2 = null;
        this.toolPanel.selectedCategory3 = null;
        this.toolPanel.selectedTime = [];
        this.toolPanel.orderParam = {};
        this.dataPanel.orderResultList = [];
    }

    changePage() {
        this.loadOrders();
    }
}
