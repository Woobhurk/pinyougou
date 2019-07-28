window.onload = () => {
    new UserStatistics().main();
};

class UserStatistics {
    toolPanel;
    dataPanel;

    userStatisticsChart;

    main() {
        this.initVc();
        this.initChart();
        this.loadUserStatistics();
    }

    initVc() {
        let _this = this;

        this.toolPanel = new Vue({
            el: '#toolPanel',
            data: {
                TIME_DELTAS: ['按小时', '按天', '按月'],
                selectedTime: [new Date(0), new Date()],
                userStatisticsParam: {
                    startTime: new Date(0),
                    endTime: new Date(),
                    timeDelta: 0
                }
            },
            methods: {
                onChangeTime: function () {
                    _this.changeTime();
                },
                onSearchUserStatistics: function () {
                    _this.searchUserStatistics();
                },
                onClearParam: function () {
                    _this.clearParam();
                }
            }
        });

        this.dataPanel = new Vue({
            el: '#dataPanel',
            data: {},
            methods: {}
        });
    }

    initChart() {
        this.userStatisticsChart = echarts.init(
            document.getElementById('userStatisticsChart'));
    }

    loadUserStatistics() {
        let userStatisticsParam = this.toolPanel.userStatisticsParam;
        let option = {
            title: {
                text: '用户人数统计'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                }
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: []
            },
            yAxis: {
                type: 'value'
            },
            series: {
                data: [],
                type: 'line',
                areaStyle: {}
            }
        };

        this.userStatisticsChart.showLoading();
        AjaxUtils.post(`${SELLER_GOODS_WEB}/userStatistics/findUserStatistics.shtml`,
            userStatisticsParam, resultInfo => {
                option.xAxis.data = resultInfo.data.horizontalDataList;
                option.series.data = resultInfo.data.verticalDataList;
                this.userStatisticsChart.hideLoading();
                this.userStatisticsChart.setOption(option);
            });
    }

    changeTime() {
        let startTime = this.toolPanel.selectedTime[0];
        let endTime = this.toolPanel.selectedTime[1];

        this.toolPanel.userStatisticsParam.startTime = startTime;
        this.toolPanel.userStatisticsParam.endTime = endTime;
    }

    searchUserStatistics() {
        this.loadUserStatistics();
    }

    clearParam() {
        this.toolPanel.selectedTime = [new Date(0), new Date()];
        this.toolPanel.userStatisticsParam = {
            startTime: new Date(0),
            endTime: new Date(),
            timeDelta: 0
        }
    }
}
