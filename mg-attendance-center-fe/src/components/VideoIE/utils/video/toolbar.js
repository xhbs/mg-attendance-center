
const toolBars = [];

export default {
    init(index) {
        if (typeof toolBars[index] === "undefined") {
            toolBars[index] = {};
        }
        if (typeof toolBars[index]["btnStatus"] === "undefined") {
            // toolBars[index]["btnStatus"] = [false, false, false, false];
            toolBars[index]["btnStatus"] = [false, false, false, false];
        }
    },
    onShowBarHandle(index, api) {
        this.init(index);
        api.SetButtonStatus(...toolBars[index].btnStatus);
    },

    // 切换录像回放时间
    realTimeChange(index, type, api){
        toolBars[index]['selectTime'] = type; //保存时间传给点击即时录像回放事件onClickHandle()0();
        api.SetInstantReplayDuration(index, type);
    },

    onClickHandle(index, type, api) {
        this.init(index);
        const BtnStatus = toolBars[index].btnStatus;
        const events = {
            // 抓图
            5() {
                const id = api.getPUIDByIndex(index),date = new Date();
                // const path = `C:\\ProgramData\HUAZHI_ocx\\${id}\\${date.getFullYear()}${date.getMonth()+1}${date.getDate()}\\${date.getTime()}.jpeg`;
                const path = api.getDataPath()
                const file = `${path}${id}\\${date.getFullYear()}${date.getMonth()+1}${date.getDate()}\\${date.getTime()}.jpeg`
                api.CapturePic(index, file);
            },
            // 本地视频下载
            6() {
                if (BtnStatus[2] === false) {
                    // 录像开始
                    const id = api.getPUIDByIndex(index),date = new Date();
                    const path = api.getDataPath()
                    const file = `${path}${id}\\${date.getFullYear()}${date.getMonth()+1}${date.getDate()}\\${date.getTime()}.dav`
                    // const path = `C:\\ProgramData\\HUAZHI_ocx\\${id}\\${date.getFullYear()}${date.getMonth()+1}${date.getDate()}\\${date.getTime()}.dav`;
                    api.StartRecordLocal(index, file, 10);
                } else {
                    // 录像暂停
                    api.StopRecordLocal(index);
                }
                BtnStatus[2] = !BtnStatus[2];
                api.SetButtonStatus(...BtnStatus);
            },
            // 及时回放
            0() {
                if(BtnStatus[3] === false){
                    // api.InstantReplay(index, sec,() => { //当点击录像回访时OCXApiJs中的instantReplay

                    // });
                    const sec = toolBars[index]['selectTime'];
                    api.InstantReplay(index, sec, ()=>{
                        api.SetButtonStatus(...BtnStatus)
                    });
                } else{
                    api.InstantStop(index);
                    BtnStatus[3] = true;
                }
                BtnStatus[3] = !BtnStatus[3];
                api.SetButtonStatus(...BtnStatus);

            },
            // 对讲
            3() {
                if (BtnStatus[1] === false) {
                    api.OpenTalk(index);
                } else {
                    api.CloseTalk(index);
                }
                BtnStatus[1] = !BtnStatus[1];
                api.SetButtonStatus(...BtnStatus);
            },
            // 声音
            4() {
                if (BtnStatus[0] === false) {
                    api.OpenSound(index);
                } else {
                    api.CloseSound(index);
                }
                BtnStatus[0] = !BtnStatus[0];
                api.SetButtonStatus(...BtnStatus);
            },
            // 关闭
            9() {
                api.CloseBtn(index, (index,PdWndIndex) => {
                        BtnStatus[index] = [false, false, false, false];
                        const arr = Object.getOwnPropertyNames(PdWndIndex);
                    if(arr.length === 0) {
                        window.video.api.hide();
                    }
                });
            },
        };
        if (events[type]) {
            events[type]();
        }
    },
};
