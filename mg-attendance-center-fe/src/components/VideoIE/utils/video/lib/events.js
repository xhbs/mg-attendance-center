/* eslint-disable */
// video.event.addVideoEvent

import { CreateRandomstring } from "./util";

class VideoEvents {
    eventTypes = {
        BTNCLICK: "BTNCLICK",
        MOUSEMOVE: "MOUSEMOVE",
        SHOWBAR: "SHOWBAR",
        RESPONSE: "RESPONSE",
        LMOUSECLIENT: "LMOUSECLIENT",
        PROGRESSCHANGED:"PROGRESSCHANGED",
        LOOPINTERVALCHANGED:"LOOPINTERVALCHANGED",
        FOCUSCHANGED:"FOCUSCHANGED",
        PROGRESSNOTIFY:"PROGRESSNOTIFY",
        OSSFILEEVENT:"OSSFILEEVENT",
        TAGNOFITY:"TAGNOFITY",
        LOCKNOFITY:"LOCKNOFITY",
        ONWHEELMOUSE: 'ONWHEELMOUSE',
        REALTIMEPLAYTIMECHANGED: 'REALTIMEPLAYTIMECHANGED', // 即时回放时间
        ONEVENTNOTIFY: 'ONEVENTNOTIFY' // 系统事件
    };

    // 注册事件
    on(event) {
        // console.log('bind event 1', event)
        if (typeof event === "string") {
            const upperEvent = event.toLocaleUpperCase();
            if (
                this.eventTypes[upperEvent] &&
                typeof this[`_${event}`] === "function"
            ) {
                // console.log('bind event2', event)
                this[`_${event}`]();
            }
        }
    }

    /*
    *  一下下划线开头的函数，都是注册监听 OCX Object 对象派发的事件
    *  然后再回调相关事件类型下的所有回调
    * */

    _onEventNotify() {
        const self = this
        // if (document.getElementById('WebIC').attachEvent) {
        //     document.getElementById('WebIC').attachEvent('OnEventNotify', function(json) {
        //         self.handle(json, self.eventTypes.ONEVENTNOTIFY)
        //     });
        // }
        // function WebICSDoubleColonEOnEventNotify(json) {
        //     self.handle(json, self.eventTypes.ONEVENTNOTIFY)
        // }
        eval(`
        function WebIC::OnEventNotify(json) {
            self.handle(json, self.eventTypes.ONEVENTNOTIFY)
        }
        `)
    }

   // 鼠标滚轮滚动
   _onWheelMouse() {
    const self = this;
    // if (document.getElementById('WebPlayWnd').attachEvent) {
    //     document.getElementById('WebPlayWnd').attachEvent('OnWheelMouse', function(index, x, y, delta) {
    //         self.handle(index, x, y, delta, self.eventTypes.ONWHEELMOUSE);
    //     });
    // }
    // function WebPlayWndSDoubleColonEOnWheelMouse(index, x, y, delta) {
    //     self.handle(index, x, y, delta, self.eventTypes.ONWHEELMOUSE);
    // }
    eval(`
    function WebPlayWnd::OnWheelMouse(index, x, y, delta) {
        self.handle(index, x, y, delta, self.eventTypes.ONWHEELMOUSE);
    }
    `)
}
    // 鼠标点击按钮
    _btnClick() {
        const self = this;
        // if (document.getElementById('WebPlayWnd').attachEvent) {
        //     document.getElementById('WebPlayWnd').attachEvent('OnBtnClicked', function(index, type) {
        //         self.handle(index, type, self.eventTypes.BTNCLICK);
        //     });
        // }
        // function WebPlayWndSDoubleColonEOnBtnClicked(index, type) {
        //     self.handle(index, type, self.eventTypes.BTNCLICK);
        // }
        eval(`
        function WebPlayWnd::OnBtnClicked(index, type) {
            self.handle(index, type, self.eventTypes.BTNCLICK);
        }
        `)
    }

    _realTimePlayTimeChanged() {
        const self = this;
        // if (document.getElementById('WebPlayWnd').attachEvent) {
        //     document.getElementById('WebPlayWnd').attachEvent('RealTimePlayTimeChanged', function(index, type) {
        //         self.handle(index, type, self.eventTypes.REALTIMEPLAYTIMECHANGED);
        //     });
        // }
        // function WebPlayWndSDoubleColonERealTimePlayTimeChanged(index, type) {
        //     self.handle(index, type, self.eventTypes.REALTIMEPLAYTIMECHANGED);
        // }
        eval(`
        function WebPlayWnd::RealTimePlayTimeChanged(index, type) {
            self.handle(index, type, self.eventTypes.REALTIMEPLAYTIMECHANGED);
        }
        `)
    } //电子放大
    _amp(){
        const self = this;
        // if (document.getElementById('WebPlayWnd').attachEvent) {
        //     document.getElementById('WebPlayWnd').attachEvent('Amp', function(x, y, w, h, pw, ph, t, wnd) {
        //         self.handle(x, y, w, h, pw, ph, t, wnd, self.eventTypes.APM);
        //     });
        // }
        // function WebPlayWndSDoubleColonEAmp(x, y, w, h, pw, ph, t, wnd) {
        // self.handle(x, y, w, h, pw, ph, t, wnd, self.eventTypes.APM);
        // }
        eval(`
        function WebPlayWnd::Amp(x, y, w, h, pw, ph, t, wnd) {
            self.handle(x, y, w, h, pw, ph, t, wnd, self.eventTypes.APM);
            }
        `)
    }
    // 鼠标移动
    _mouseMove() {
        const self = this;
        // if (document.getElementById('WebPlayWnd').attachEvent) {
        //     document.getElementById('WebPlayWnd').attachEvent('OnMouseMove', function(index) {
        //         self.handle(index, self.eventTypes.MOUSEMOVE);
        //     });
        // }
        // function WebPlayWndSDoubleColonEOnMouseMove(index) {
        //     self.handle(index, self.eventTypes.MOUSEMOVE);
        // }
        eval(`
        function WebPlayWnd::OnMouseMove(index) {
            self.handle(index, self.eventTypes.MOUSEMOVE);
        }
        `)

    }

    // 工具栏显示
    _showBar() {
        const self = this;
        // if (document.getElementById('WebPlayWnd').attachEvent) {
        //     document.getElementById('WebPlayWnd').attachEvent('OnShowBar', function(index) {
        //         self.handle(index, self.eventTypes.SHOWBAR);
        //     });
        // }
        // function WebPlayWndSDoubleColonEOnShowBar(index) {
        //     self.handle(index, self.eventTypes.SHOWBAR);
        // }
        eval(`
        function WebPlayWnd::OnShowBar(index) {
            self.handle(index, self.eventTypes.SHOWBAR);
        }
        `)
    }
    // 录播
    _loopIntervalChanged(){
        const self =this;
        // if (document.getElementById('WebPlayWnd').attachEvent) {
        //     document.getElementById('WebPlayWnd').attachEvent('loopIntervalChanged', function(index, sec) {
        //         self.handle(index,sec,self.eventTypes.LOOPINTERVALCHANGED);
        //     });
        // }
        // function  WebPlayWndSDoubleColonEloopIntervalChanged(index,sec){
        //     self.handle(index,sec,self.eventTypes.LOOPINTERVALCHANGED);
        // }
        eval(`
        function  WebPlayWnd::loopIntervalChanged(index,sec){
            self.handle(index,sec,self.eventTypes.LOOPINTERVALCHANGED);
        }
        `)
    }
    // 事件回调
    _response() {
        const self = this;
        // if (document.getElementById('WebIC').attachEvent) {
        //     document.getElementById('WebIC').attachEvent('OnResponseHandle', function(id, results) {
        //         self.handle(id, results, self.eventTypes.RESPONSE);
        //     });
        // }

        // function WebICSDoubleColonEOnResponseHandle(id, results) {
        //     self.handle(id, results, self.eventTypes.RESPONSE);
        // }
        eval(`
        function WebIC::OnResponseHandle(id, results) {
            console.log("ocx_repsonse_id", id)
            self.handle(id, results, self.eventTypes.RESPONSE);
        }
        `)
    }

    _tagNofity(){
        const self = this;
        // if (document.getElementById('Progross').attachEvent) {
        //     document.getElementById('Progross').attachEvent('tagNofity', function(index,flag,time,content) {
        //         self.handle(index,flag,time,content, self.eventTypes.TAGNOFITY);
        //     });
        // }
        // function ProgrossSDoubleColonEtagNofity(index,flag,time,content) {
        //     self.handle(index,flag,time,content, self.eventTypes.TAGNOFITY);
        // }
        eval(`
        function Progross::tagNofity(index,flag,time,content) {
            self.handle(index,flag,time,content, self.eventTypes.TAGNOFITY);
        }
        `)
    }

    _lockNofity(){
        const self = this;
        // if (document.getElementById('Progross').attachEvent) {
        //     document.getElementById('Progross').attachEvent('lockNofity', function(index, flag, beginTime, endTime) {
        //         self.handle(index, flag, beginTime, endTime, self.eventTypes.LOCKNOFITY)
        //     });
        // }
        // function ProgrossSDoubleColonElockNofity(index, flag, beginTime, endTime,) {
        //     self.handle(index, flag, beginTime, endTime, self.eventTypes.LOCKNOFITY);
        // }
        eval(`
        function Progross::lockNofity(index, flag, beginTime, endTime) {
            self.handle(index, flag, beginTime, endTime, self.eventTypes.LOCKNOFITY);
        }
        `)
    }

    _ossFileEvent(){
        const self = this;
        // if (document.getElementById('WebIC').attachEvent) {
        //     document.getElementById('WebIC').attachEvent('OnOSSFileDLEventHandle', function(json) {
        //         self.handle(json, self.eventTypes.OSSFILEEVENT);
        //     });
        // }
        // function WebICSDoubleColonEOnOSSFileDLEventHandle(json) {
        //     self.handle(json, self.eventTypes.OSSFILEEVENT);
        // }
        eval(`
        function WebIC::OnOSSFileDLEventHandle(json) {
            self.handle(json, self.eventTypes.OSSFILEEVENT);
        }
        `)
    }

    _LMouseClient() {
        const self = this;
        // if (document.getElementById('WebPlayWnd').attachEvent) {
        //     document.getElementById('WebPlayWnd').attachEvent('OnLMouseClient', function(index) {
        //         self.handle(index, self.eventTypes.LMOUSECLIENT);
        //     });
        // }
        // function WebPlayWndSDoubleColonEOnLMouseClient(index) {
        //     self.handle(index, self.eventTypes.LMOUSECLIENT);
        // }
        eval(`
        function WebPlayWnd::OnLMouseClient(index) {
            self.handle(index, self.eventTypes.LMOUSECLIENT);
        }
        `)
    }

    _RMouseClient() {
        const self = this;
        // if (document.getElementById('WebPlayWnd').attachEvent) {
        //     document.getElementById('WebPlayWnd').attachEvent('OnRMouseClient', function(index) {
        //         self.handle(index, self.eventTypes.RMOUSECLIENT);
        //     });
        // }
        // function WebPlayWndSDoubleColonEOnRMouseClient(index) {
        //     self.handle(index, self.eventTypes.RMOUSECLIENT);
        // }
        eval(`
        function WebPlayWnd::OnRMouseClient(index) {
            self.handle(index, self.eventTypes.RMOUSECLIENT);
        }
        `)
    }

    // 进度条事件
    _progressChanged(){
        const self = this;
        // if (document.getElementById('Progross').attachEvent) {
        //     document.getElementById('Progross').attachEvent('progressChanged', function(index,flag,progressTime,progressFrame) {
        //         self.handle(index,flag,progressTime,progressFrame, self.eventTypes.PROGRESSCHANGED);
        //     });
        // }
        // function ProgrossSDoubleColonEprogressChanged(index,flag,progressTime,progressFrame) {
        //     self.handle(index,flag,progressTime,progressFrame, self.eventTypes.PROGRESSCHANGED);
        // }
        eval(`
        function Progross::progressChanged(index,flag,progressTime,progressFrame) {
            self.handle(index,flag,progressTime,progressFrame, self.eventTypes.PROGRESSCHANGED);
        }
        `)
    }

    // 播放列表焦点改变事件
    _focusChanged(){
        const self = this;
        // if (document.getElementById('Progross').attachEvent) {
        //     document.getElementById('Progross').attachEvent('focusChanged', function(index,flag) {
        //         self.handle(index,flag, self.eventTypes.FOCUSCHANGED);
        //     });
        // }
        // function ProgrossSDoubleColonEfocusChanged(index,flag) {
        //     self.handle(index,flag, self.eventTypes.FOCUSCHANGED);
        // }
        eval(`
        function Progross::focusChanged(index,flag) {
            self.handle(index,flag, self.eventTypes.FOCUSCHANGED);
        }
        `)
    }

    // 进度通知 getProgress触发
    _progressNotify(){
        const self = this;
        // if (document.getElementById('Progross').attachEvent) {
        //     document.getElementById('Progross').attachEvent('progressNotify', function(index,progressTime,progressFrame) {
        //         self.handle(index,progressTime,progressFrame, self.eventTypes.PROGRESSNOTIFY);
        //     });
        // }
        // function ProgrossSDoubleColonEprogressNotify(index,progressTime,progressFrame) {
        //     self.handle(index,progressTime,progressFrame, self.eventTypes.PROGRESSNOTIFY);
        // }
        eval(`
        function Progross::progressNotify(index,progressTime,progressFrame) {
            self.handle(index,progressTime,progressFrame, self.eventTypes.PROGRESSNOTIFY);
        }
        `)
    }

  /**
   * 添加-固定事件类型监听
   * @param type eventType.id 结构。eventType 表示事件类型，id 表示此类型下的子 ID
   * @param callback
   */
    addVideoEvent(type, callback) {
        if (typeof type === "string") {
            const event = type.split(".")[0];

            let id = type.split(".")[1];

            const upperType = event.toLocaleUpperCase();

            // 判断是否支持，申请监听的事件类型
            if (this.eventTypes[upperType]) {
                // 初始化某事件类型监听容器
                if (!this[upperType]) this[upperType] = {};
                // 随机一个字符串，当作 key ,callback 作为 value 加入容器中
                if (!id) id = CreateRandomstring();
                this[upperType][id] = callback;
            }
        }
    }

  /**
   * 添加-固定事件类型监听
   * @param type
   */
  deleteVideoEvent(type) {
        if (typeof type === "string") {
            const event = type.split(".")[0];

            const id = type.split(".")[1];

            const upperType = event.toLocaleUpperCase();
            if (this.eventTypes[upperType]) {
                if (!id) {
                    delete this[upperType];
                } else {
                    delete this[upperType][id];
                }
            }
        }
    }

  /**
   * 触发-固定事件类型
   */
  handle() {
        const type = arguments[arguments.length - 1];

        // 遍历回调 某事件容器
        for (const key in this[type]) {
            this[type][key](...arguments);
        }
    }
}
export default VideoEvents;
