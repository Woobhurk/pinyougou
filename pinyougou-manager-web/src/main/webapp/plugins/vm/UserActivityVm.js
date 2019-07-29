window.onload = () => {
    new UserActivityVm().main();
};

class UserActivityVm {
    toolPanel;
    dataPanel;

    userActivityTimeChart;
    userActivityCountChart;

    main() {
        this.initVc();
        this.initChart();
        this.loadUserActivity();
        this.loadUserActivityTime();
        this.loadUserActivityCount();
    }

    initVc() {
        let _this = this;

        this.toolPanel = new Vue({
            el: '#toolPanel',
            data: {
                TIME_UNIT: ['按小时', '按天', '按月'],
                selectedTime: [
                    new Date(2017, 1, 1, 0, 0, 0),
                    new Date()
                ],
                userActivityParam: {
                    startTime: new Date(2017, 1, 1, 0, 0, 0),
                    endTime: new Date(),
                    timeUnit: 2
                }
            },
            methods: {
                onChangeTime: function () {
                    _this.changeTime();
                },
                onSearchUserActivity: function () {
                    _this.searchUserActivity();
                },
                onClearParam: function () {
                    _this.clearParam();
                }
            }
        });

        this.dataPanel = new Vue({
            el: '#dataPanel',
            data: {
                selectedTab: 'userActivity',
                userActivityResultList: [],
                mostActiveUserList: [],
                leastActiveUserList: []
            },
            methods: {}
        });
    }

    initChart() {
        this.userActivityTimeChart = echarts.init(
            document.getElementById('userActivityTimeChart'));
        this.userActivityCountChart = echarts.init(
            document.getElementById('userActivityCountChart'));
    }

    loadUserActivity() {
        let userActivityParam = this.toolPanel.userActivityParam;

        AjaxUtils.post(`${SELLER_GOODS_WEB}/userActivity/findUserActivity.shtml`,
            userActivityParam, resultInfo => {
                let userActivityResultList = resultInfo.data;

                this.dataPanel.mostActiveUserList = userActivityResultList.slice(0, 10);
                userActivityResultList.reverse();
                this.dataPanel.leastActiveUserList = userActivityResultList.slice(0, 10);
            });
    }

    loadUserActivityTime() {
        let userActivityParam = this.toolPanel.userActivityParam;
        let chartOption = {
            title: {
                text: '用户活跃时间统计'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: '{b} {c}'
            },
            xAxis: {
                type: 'time'
            },
            yAxis: {
                max: 24
            },
            dataZoom: [
                {
                    type: 'slider',
                    show: true,
                    xAxisIndex: [0],
                    start: 0,
                    end: 100
                },
                {
                    type: 'slider',
                    show: true,
                    yAxisIndex: [0],
                    left: '93%',
                    start: 0,
                    end: 100
                },
                {
                    type: 'inside',
                    xAxisIndex: [0],
                    start: 0,
                    end: 100
                },
                {
                    type: 'inside',
                    yAxisIndex: [0],
                    start: 0,
                    end: 100
                }
            ],
            series: {
                symbolSize: 10,
                data: [],
                type: 'scatter'
            }
        };

        this.userActivityTimeChart.showLoading();
        AjaxUtils.post(`${SELLER_GOODS_WEB}/userActivity/findUserActivityTime.shtml`,
            userActivityParam, resultInfo => {
                //chartOption.series.data = resultInfo.data.dataList;
                chartOption.series.data = this.convertUserActivityTimeUnit(
                    resultInfo.data.dataList);
                this.userActivityTimeChart.hideLoading();
                this.userActivityTimeChart.setOption(chartOption);
            });
    }

    loadUserActivityCount() {
        let userActivityParam = this.toolPanel.userActivityParam;
        let chartOption = {
            title: {
                text: '用户活跃人数统计'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: '{b} {c}人'
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: []
            },
            yAxis: {
                type: 'value'
            },
            dataZoom: [
                {
                    type: 'slider',
                    show: true,
                    xAxisIndex: [0],
                    start: 0,
                    end: 100
                },
                {
                    type: 'slider',
                    show: true,
                    yAxisIndex: [0],
                    left: '93%',
                    start: 0,
                    end: 100
                },
                {
                    type: 'inside',
                    xAxisIndex: [0],
                    start: 0,
                    end: 100
                },
                {
                    type: 'inside',
                    yAxisIndex: [0],
                    start: 0,
                    end: 100
                }
            ],
            series: {
                data: [],
                type: 'line',
                areaStyle: {}
            }
        };

        this.userActivityCountChart.showLoading();
        AjaxUtils.post(`${SELLER_GOODS_WEB}/userActivity/findUserActivityCount.shtml`,
            userActivityParam, resultInfo => {
                chartOption.xAxis.data = resultInfo.data.horizontalDataList;
                chartOption.series.data = resultInfo.data.verticalDataList;
                this.userActivityCountChart.hideLoading();
                this.userActivityCountChart.setOption(chartOption);
            });
    }

    changeTime() {
        let selectedTime = this.toolPanel.selectedTime;

        this.toolPanel.userActivityParam.startTime = selectedTime[0];
        this.toolPanel.userActivityParam.endTime = selectedTime[1];
    }

    searchUserActivity() {
        let selectedTab = this.dataPanel.selectedTab;

        switch (selectedTab) {
        case "userActivity":
            this.loadUserActivity();
            break;
        case 'userActivityTime':
            this.loadUserActivityTime();
            break;
        case 'userActivityCount':
            this.loadUserActivityCount();
            break;
        default:
            console.warn("Unknown tab " + selectedTab);
            break;
        }
    }

    clearParam() {
        this.toolPanel.selectedTime = [
            new Date(2017, 1, 1, 0, 0, 0),
            new Date()
        ];
        this.toolPanel.userActivityParam = {
            startTime: new Date(2017, 1, 1, 0, 0, 0),
            endTime: new Date(),
            timeUnit: 2
        };
    }

    convertUserActivityTimeUnit(userActivityTimeResult) {
        let userActivityParam = this.toolPanel.userActivityParam;
        let newUserActivityResult = [];

        for (let dataPair of userActivityTimeResult) {
            let dateMilliseconds = dataPair[0];
            let hourMilliseconds = dataPair[1];
            let newDataPair = [];

            //newDataPair[0] = (dateMilliseconds - userActivityParam.startTime.valueOf())
            //    / 24 / 3600 / 1000;
            newDataPair[0] = dateMilliseconds;
            newDataPair[1] = hourMilliseconds / 3600 / 1000;
            newUserActivityResult.push(newDataPair);
        }

        return newUserActivityResult;
    }
}
