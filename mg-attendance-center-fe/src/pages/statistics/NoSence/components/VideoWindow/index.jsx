/*
 * @Author: xiedan WX042
 * @since: 2020-09-02 11:00:48
 * @lastTime: 2020-10-09 18:13:32
 * @FilePath: \biz-scene-coree:\hz\biz-scene-attendance-web\src\pages\statistics\NoSence\components\VideoWindow\index.jsx
 * @Description: 考勤看板---视频播放器区域
 */

import React, { useState, useRef } from 'react';
// import html2canvas from 'html2canvas';
import VideoChrome from '../VideoChrome';
import ToolBar from "./toolbar";
import { downloadImgFile } from '@utils/utils';
import styles from './index.less';

const VideoWindow = (props) => {
    const { handleCloseEvent, selectedDevice, getCurrentItemValue } = props;
    const [splitScreen, setSplitScreen] = useState(1);
    const videoChromeRef = useRef(null);

    // 分屏
    const changeScreen = (data) => {
        setSplitScreen(data);
    }

    // 关闭所有视频
    const closeAllVideo = () => {
        const { current } = videoChromeRef;
        if(current) {
            current.closeAllVideo();
        }
        handleCloseEvent()
    }

    // 批量抓拍
    // const handleCapture = () => {
    //     html2canvas(document.getElementById('videoWapper')).then((canvas) => {
    //         const imgUrl = canvas.toDataURL();
    //         console.log('imgUrl=====::', imgUrl)
    //         downloadImgFile(`批量视频抓拍.png`, imgUrl)
    //     });  
    // }

    const handleCapture = () => {
        for (let key = 0; key < splitScreen; key++) {
            const { current } = window[`sceneVideoPlayer${key}`] || {};
            if(current && !current.error) {
                const imgBase64 = current.capture();
                downloadImgFile(`视频截图${key + 1}.png`, imgBase64)
            }
		}
    };

    return (
        <div
            style={{
                width: "100%",
                height: "100%",
            }}
        >   
            <VideoChrome
                handleCloseEvent={handleCloseEvent}
                splitScreen={splitScreen}
                selectedDevice={selectedDevice}
                getCurrentItemValue={getCurrentItemValue}
                ref={videoChromeRef}
            />
            {/*视频容器*/}
            <div className={styles.toolBarWarper}>
                {
                    <ToolBar
                        splitScreen={splitScreen}
                        changeScreen={changeScreen}
                        disableSplit={false}
                        closeAllVideo={closeAllVideo}
                        handleCapture={handleCapture}
                    />
                }
            </div>

        </div>
    )
}


VideoWindow.defaultProps = {
    // data: [], // videos数据
    handleCloseEvent: () => {}, // 关闭视频的回调
};

export default VideoWindow;