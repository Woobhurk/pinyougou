window.onload = () => {
    new UserVm().main();
};

class UserVm {
    toolPanel;
    dataPanel;
    pagePanel;

    main() {
        this.initVc();
        this.loadUserList();
    }

    initVc() {
        let _this = this;

        this.toolPanel = new Vue({
            el: '#toolPanel',
            data: {
                selectedTime: [],
                userParam: {}
            },
            methods: {
                onChangeDate: function () {
                    _this.changeDate();
                },
                onSearchUserList: function () {
                    _this.searchUserList();
                },
                onClearUserParam: function () {
                    _this.clearUserParam();
                }
            }
        });

        this.dataPanel = new Vue({
            el: '#dataPanel',
            data: {
                userList: [],
                selectedUser: null,
                selectedTime: new Date(Date.now() + 90 * 24 * 3600 * 1000),
                freezeDialogVisible: false
            },
            methods: {
                onOpenFreezeDialog: function (user) {
                    _this.openFreezeDialog(user);
                },
                onFreezeUser: function () {
                    _this.freezeUser();
                },
                onUnfreezeUser: function (user) {
                    _this.unfreezeUser(user);
                },
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

    loadUserList() {
        let pageNum = this.pagePanel.pageNum;
        let pageSize = this.pagePanel.pageSize;
        let userParam = this.toolPanel.userParam;

        AjaxUtils.post(`${SELLER_GOODS_WEB}/user/findByPageParam/${pageNum}/${pageSize}.shtml`,
            userParam, resultInfo => {
                this.dataPanel.userList = resultInfo.data.dataList;
                this.pagePanel.total = resultInfo.data.total;
            });
    }

    changeDate() {
        let selectedTime = this.toolPanel.selectedTime;

        this.toolPanel.userParam.startTime = selectedTime[0];
        this.toolPanel.userParam.endTime = selectedTime[1];
    }

    searchUserList() {
        this.loadUserList();
    }

    clearUserParam() {
        this.toolPanel.selectedTime = [];
        this.toolPanel.userParam = {};
    }

    openFreezeDialog(user) {
        this.dataPanel.selectedUser = user;
        this.dataPanel.freezeDialogVisible = true;
    }

    freezeUser() {
        let user = this.dataPanel.selectedUser;
        let selectedTime = this.dataPanel.selectedTime;
        let unfreezeTime = new Date(Date.now() + 90 * 24 * 3600 * 1000);

        if (selectedTime === null
            || unfreezeTime.valueOf() > selectedTime.valueOf()) {
            user.unfrozen = unfreezeTime;
            this.dataPanel.$notify({
                title: '警告',
                message: '冻结时间最短为90天',
                type: 'warning'
            });
        } else {
            user.unfrozen = selectedTime;
        }

        this.dataPanel.freezeDialogVisible = false;

        AjaxUtils.post(`${SELLER_GOODS_WEB}/user/update.shtml`, user,
            resultInfo => {
                this.loadUserList();
            });
    }

    unfreezeUser(user) {
        user.unfrozen = null;

        AjaxUtils.post(`${SELLER_GOODS_WEB}/user/update.shtml`, user,
            resultInfo => {
                this.loadUserList();
            });
    }

    changePage() {
        this.loadUserList();
    }
}
