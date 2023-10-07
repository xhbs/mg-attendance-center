// 事件绑定
// 创建video
// api

import * as utils from "./lib/util.js";
import VideoEvents from "./lib/events.js";
import create from "./lib/create.js";
import OCXApi from "./lib/OCXApi.js";
import toolbar from "./toolbar.js";

/**
 * @description 视频创建周期
 */
const videoCreate = {
    /**
     * @description  创建视频窗口前执行
     */
    willcreateWebPlayWnd({ container, style, classid }) {},

    /**
     * @description  创建视频窗口后执行
     */
    createdWebPlayWnd() {

        // 此处 this = window.video
        this.events.on("btnClick");
        this.events.on("amp");
        this.events.on("mouseMove");
        this.events.on("showBar");
        this.events.on("LMouseClient");
        this.events.on("loopIntervalChanged");
        this.events.on("onWheelMouse");
        this.events.on('realTimePlayTimeChanged');
        this.api.ocxUI = this.webPlayWnd;
    },
    createdWebIC() {
        this.events.on("response");
        this.events.on("ossFileEvent");
        this.events.on('onEventNotify')
        this.api.ocxContral = this.webIC;
    },
    willcreateWebIC({ container, style, classid }) {},

    createWebPlayWnd({ container, style, classid }) {
        const options = { container, style, classid };
        this.willcreateWebPlayWnd(options);
        this.webPlayWnd = create.createWebPlayWnd(options);
        this.createdWebPlayWnd();
        return this.webPlayWnd
    },
    createWebIC({ container, style, classid }) {
        const options = { container, style, classid };
        this.willcreateWebIC(options);
        this.webIC = create.createWebIC(options);
        this.createdWebIC();
        return this.webIC
    },
    createdProgross(){
        this.events.on("progressChanged");
        this.events.on("focusChanged");
        this.events.on("progressNotify");
        this.events.on("tagNofity");
        this.events.on("lockNofity");
        this.api.progross = this.progross;
    },
    createProgross({ container, style, classid }){
        const options = { container, style, classid };
        this.progross = create.createProgross(options);
        this.createdProgross();
        return this.progross
    },

    willcreateMap({container, style}) {},

    createMap({container, style}){
        const options = { container, style };
        this.Map = create.createMap(options);
    },
};

export default () => {
    // 初始化全局 window.video 变量基础功能
    const video = (window.video = {
        utils,
        ...videoCreate,
    });
    // new 防止多个 OCX 之间相互干扰
    // 而全局 video 表示一次只能操作一个 OCX
    video.events = new VideoEvents();
    video.api = new OCXApi();


    // 绑定异步回调
    // 注意区分 video.utils.asyncEvents 和 video.events
    // 前者是通过事件类型绑定、触发，某事件类型被触发时，会回调版定在此事件上得所有回调
    // 后者是通过 ID 触发，每次只能回调一个回调。

    // 监听 OCX 所有异步接口的回调
    video.events.addVideoEvent("response.default", (id, data) => {
        let results = data;
        if (typeof results === "string") {
            console.log("~~~~", results)
            results = JSON.parse(results);
        }
        video.utils.asyncEvents.emit(id, results);
    });

    video.events.addVideoEvent("showBar.default", index => {
        toolbar.onShowBarHandle(index, video.api);
    });

    video.events.addVideoEvent("btnClick.default", (index, type) => {
        toolbar.onClickHandle(index, type, video.api);
    });

    video.events.addVideoEvent("realTimePlayTimeChanged.default", (index,type) => {
        toolbar.realTimeChange(index, type, video.api);
    })

    video.version = "1.0.0";
};
