window.onload = () => {
    new OrderItemChartVm().main();
};

class OrderItemChartVm {
    toolPanel;
    chartPanel;

    sunburstChart;

    main() {
        this.initVc();
        this.initChart();
        this.loadCategory1List();
        this.loadChartData();
    }

    initVc() {
        let _this = this;

        this.toolPanel = new Vue({
            el: '#toolPanel',
            data: {
                category1List: [],
                category2List: [],
                category3List: [],
                selectedCategory1: null,
                selectedCategory2: null,
                selectedCategory3: null,
                selectedDate: [
                    new Date(2000, 10, 10, 10, 10),
                    new Date(2019, 10, 11, 10, 10)
                ],
                orderItemChartParam: {
                    parentId: 0,
                    startTime: new Date(2000, 10, 10, 10, 10),
                    endTime: new Date(2019, 10, 11, 10, 10)
                }
            },
            methods: {
                onChangeCategory1: function () {
                    _this.changeCategory1();
                },
                onChangeCategory2: function () {
                    _this.changeCategory2();
                },
                onChangeCategory3: function () {
                    _this.loadChartData();
                },
                onChangeDate: function () {
                    _this.changeDate();
                },
                onSearchChart: function () {
                    _this.loadChartData();
                },
                onClearChartParam: function () {
                    _this.clearChartParam();
                }
            }
        });

        this.chartPanel = new Vue({
            el: '#chartPanel',
            data: {},
            methods: {}
        });
    }

    initChart() {
        let chartOption = {
            series: {
                type: 'sunburst',
                radius: 300,
                data: []
            }
        };

        this.sunburstChart = echarts.init(document.getElementById('sunburstChart'));
        this.sunburstChart.setOption(chartOption);
    }

    loadCategory1List() {
        AjaxUtils.post(`${SELLER_GOODS_WEB}/itemCat/findByParentId/0.shtml`,
            null, resultInfo => {
                this.toolPanel.category1List = resultInfo;
                this.toolPanel.category2List = [];
                this.toolPanel.category3List = [];
                this.toolPanel.selectedCategory2 = null;
                this.toolPanel.selectedCategory3 = null;
            });

        this.loadChartData();
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

        this.toolPanel.orderItemChartParam.parentId = parentId;
        this.loadChartData();
    }

    changeCategory2() {
        let parentId = this.toolPanel.selectedCategory2;

        AjaxUtils.post(`${SELLER_GOODS_WEB}/itemCat/findByParentId/${parentId}.shtml`,
            null, resultInfo => {
                this.toolPanel.category3List = resultInfo;
                this.toolPanel.selectedCategory3 = null;
            });

        this.toolPanel.orderItemChartParam.parentId = parentId;
        this.loadChartData();
    }

    changeDate() {
        let startTime = this.toolPanel.selectedDate[0];
        let endTime = this.toolPanel.selectedDate[1];

        this.toolPanel.orderItemChartParam.startTime = startTime;
        this.toolPanel.orderItemChartParam.endTime = endTime;
    }

    clearChartParam() {
        this.toolPanel.category2List = [];
        this.toolPanel.category3List = [];
        this.toolPanel.selectedCategory1 = null;
        this.toolPanel.selectedCategory2 = null;
        this.toolPanel.selectedCategory3 = null;
        this.toolPanel.orderItemChartParam.parentId = null;
        this.toolPanel.orderItemChartParam.startTime = new Date(
            2000, 10, 10, 10, 10);
        this.toolPanel.orderItemChartParam.endTime = new Date(
            2019, 10, 11, 10, 10);
        this.loadChartData();
    }

    loadChartData() {
        let orderItemChartParam = this.toolPanel.orderItemChartParam;

        AjaxUtils.post(`${SELLER_GOODS_WEB}/orderItemChart/countOrderItem.shtml`,
            orderItemChartParam, resultInfo => {
                this.sunburstChart.setOption({
                    series: {
                        type: 'sunburst',
                        radius: 300,
                        data: resultInfo.data
                    }
                });
            });
    }
}
