/*
 * @Author: hesisi 00444
 * @Date: 2020-05-08 10:21:50
 * @Description: chrome视频播放插件
 */
import React from "react";
import { Row, Col } from "antd";
import ClassNames from 'classnames';
import Cookies from 'js-cookie';
import { VideoPlayer } from "@hz-components/react-base";
import { downloadImgFile } from '@utils/utils';
import styles from "./index.less";

// vPlay配置
const progress = true;
// 分屏时每个视频的span值
const spanMap = {
	1: 24,
	4: 12,
	9: 8,
	16: 6,
}
// 分屏时每个视频的高度
const heightMap = {
	1: '100%',
	4: '50%',
	9: '33.33%',
	16: '25%',
}

class VideoChrome extends React.Component {
	static defaultProps = {
		handleCloseEvent: () => { }, // 父组件回调
		selectedDevice: '', // 当前被点击的设备的cameraid
		splitScreen: 1,  // 分屏数
	};

	state = {
		currentVideo: 0,  // 当前选中的视频
		videoItemArr: Array.from({length: 16}, () => ''), // 视频列表
	}

	shouldComponentUpdate(nextProps, nextState) {
		const { selectedDevice: newSelectedDevice, splitScreen: newSplitScreen } = nextProps;
		const { selectedDevice, splitScreen, getCurrentItemValue } = this.props;

		const { videoItemArr: NvideoItemArr, currentVideo: NcurrentVideo } = nextState;
		const { videoItemArr, currentVideo } = this.state;
		if(selectedDevice !== newSelectedDevice) {
			let newVideoItemArr = [...NvideoItemArr];

			// 存下当前点击的视频id，方便替换其他视频
			getCurrentItemValue(newSelectedDevice);

			newVideoItemArr[currentVideo] = newSelectedDevice;
			this.setState({
				videoItemArr: newVideoItemArr,
			})
			return true;
		}
		if(newSplitScreen !== splitScreen) {
			// 改变分屏时，切换选中的目标为第一个视频
			this.onVideoItem(0, videoItemArr[0])
			return true;
		}
		if(NvideoItemArr !== videoItemArr) {
			return true;
		}
		if(NcurrentVideo !== currentVideo) {
			return true;
		}
		return false;
	}

	// 渲染视频
	renderVideos = (video, key) => {
		const props = {
			type: "preview",
			camera_id: video || "",
			is_local: Cookies.get('outer') || 0,
			progress,
		};

		return (
			<VideoPlayer
				{...props}
				width="100%"
				height="100%"
				key={`mainVideo${key}`}
				getRef={ref => {
					window[`sceneVideoPlayer${key}`] = ref;
				}}
			/>
		);
	}

	// 抓取视频图
	handleCapture = (k) => {
		const { current } = window[`sceneVideoPlayer${k}`];
		if (current && !current.error) {
			const imgBase64 = current.capture();
			downloadImgFile(`视频截图${k + 1}.png`, imgBase64);
		}
	}

	// 关闭视频
	closeVideo = (k, id) => {
		const { current } = window[`sceneVideoPlayer${k}`];
		if (current) {
			current.pause();
			const { handleCloseEvent } = this.props;
			const { videoItemArr } = this.state;
			let newVideoItemArr = [...videoItemArr];
			newVideoItemArr[k] = '';
			this.setState({
				videoItemArr: newVideoItemArr,
			})
			if (handleCloseEvent) {
				handleCloseEvent(k, id);
			}
		}
	}

    // 关闭所有视频
    closeAllVideo = () => {
		const { videoItemArr } = this.state;
		
        for (let key = 0; key < videoItemArr.length; key++) {
            const { current } = window[`sceneVideoPlayer${key}`] || {};
            if(current) {
				current.pause();
            }
		}
		this.setState({
			videoItemArr: Array.from({length: 16}, () => ''),
		})
    }

	// 点击单个视频
	onVideoItem = (k, v) => {
		const { getCurrentItemValue } = this.props;
		this.setState({
			currentVideo: k,
		})
		// 存下当前点击的视频id，方便替换其他视频
		getCurrentItemValue(v);
	}

	render() {
		const { splitScreen } = this.props;
		const { currentVideo, videoItemArr } = this.state;
		// console.log('videoItemArr======::', videoItemArr)
		return (
			<div className={styles.videoWapper} id='videoWapper'>
				<Row style={{ height: '100%' }}>
					{
						videoItemArr.map((v, k) => {
							return (
								<Col key={k} span={spanMap[splitScreen]} className={styles.videoItemBox} style={{ height: heightMap[splitScreen] }}>
									<div
										key={k}
										className={ClassNames({
											[`${styles.videoItem}`]: true,
											[`${styles.videoItemEmpty}`]: !v,
											[`${styles.isSelected}`]: splitScreen > 1 && currentVideo === k,
										})}
										onClick={(e) => this.onVideoItem(k, v)}
									>
										{
											v && (
												<>
													<div className={styles.videoTools}>
														{/* <Icon
															title="图上监控"
															type="hz-position"
															style={{ verticalAlign: 'top', fontSize: 16, color: '#fff', marginTop: '2px' }}
														/> */}
														<i title="抓拍" className={styles.shootIcon} onClick={() => this.handleCapture(k)} />
														<i title="关闭" className={styles.closeIcon} onClick={() => this.closeVideo(k, v)} />
													</div>
													{this.renderVideos(v, k)}
												</>
											)
										}
									</div>
								</Col>
							)
						})
					}
				</Row>
			</div>
		);
	}
}

export default VideoChrome;
