import React, { PureComponent } from 'react';
// import { PersonControlAlarm, VehicleControlAlarm } from '@hz-components/biz-scene-open';

export default class Index extends PureComponent {
    static defaultProps = {
        
            modalProps: {
                title:"人员布控详情",
            },
            // 自定义操作按鈕
            custOperation: (record) => {
                return (
                <span onClick={() => {
                    console.log("=================record:",record);
                }}>跨境追踪</span>
                );
            },
            imgToolBar: { // 详情框图片操作按钮配置
                fullscreen: true, // 图片放大
                capture: true, // 截图搜索
                collect: true, // 收藏
                video: true, // 视频回放
                down: false, // 下载图片
            },
            eventVideoOutPut: (obj) => {
                console.log('=====videoEvent', obj)
            },  // 外部传入视频回放事件
            eventCapture: (obj) => {
                console.log('=====captureEvent', obj)
            },  // 外部传入截图搜索事件
            detailOperationButton: [
                {
                name:'跨境追踪',
                eventFun: (obj) => {
                    console.log(obj);
                },
                },
            ], // 详情框外部自定义的按钮
    }

    render() {
        return (
            <div />
            // <VehicleControlAlarm {...this.props} />
        )
    }
}