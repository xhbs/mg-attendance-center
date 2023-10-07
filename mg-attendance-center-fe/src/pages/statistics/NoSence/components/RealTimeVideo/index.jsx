/*
 * @Author: xiedan WX042
 * @since: 2020-09-01 20:26:34
 * @lastTime: 2020-11-19 17:52:36
 * @FilePath: \biz-scene-attendance-web\src\pages\statistics\NoSence\components\RealTimeVideo\index.jsx
 * @Description: 考勤看板内容
 */
import React, { Component } from 'react';
import { message } from 'antd';
import LeftTree from '../LeftTree';
import Record from '../Record';
import VideoWindow from '../VideoWindow';
import VideIE from '@components/VideoIE';
import HzSocket from '@utils/websocket/socket.js';
import { getExplorer } from '@utils/utils';
import styles from './realTimeVideo.less';

const isPro = process.env.NODE_ENV === 'production';

message.config({
	top: 40,
	duration: 2,
	maxCount: 10,
});

export default class RealTimeVideo extends Component {
    state = {
		dragFlag: false, // 添加一个变量，用来判断更新的时候ocxUI存在
		treeClose: true,  // 收缩 or 展开
		totalData: [], // 推送数据
		// totalData: [
		//   	{
		// 		base_url: "http://192.168.111.52:9003/?namelists/dn@2_1/1/1-2e29ec6-21ba0",
		// 		department_name: '紫光画质紫光画质紫光画质紫光画质紫光画质紫光画质',
		// 		device_code: "10000000051191000001",
		// 		device_name: "人脸卡口1234567890_设备_1_通道_1",
		// 		person_id: "50010119950821313X",
		// 		person_name: "宋先强宋先强宋先强宋先强宋先强",
		// 		person_no: "485",
		// 		result: "正常",
		// 		similarity: 99,
		// 		site_id: 758005167177732100,
		// 		site_name: "测试922",
		// 		snapshot_url: "http://192.168.111.51:9003/?dispositionfaces/dn@1_1/1/1-15e2797b-3762",
		// 		time: 1600864936206,
		// 	},
		// ],
		majorData: [],
		seconData: [],
		socket: null,
		point: '', // 考勤点
		pointArr: [], // 点击过得所有考勤点，用于避免重复点击创建推送

		videoData: [],  // 所有播放中的设备   ['10000000001311000007', '10000000051191000002', '', '']
		selectedDevice: '',   // 资源树最后一次被点击的设备的 id
		currentItemValue: '',   // 播放列表当前选中的目标的 id

		isIE: false,  // 是否为IE浏览器
		busyScreen: Array.from({length: 16}).map(() => false),
    }

    componentDidMount() {
		const explorer = getExplorer();
		if (explorer !== 'ie' && explorer !== 'Edge') {
			this.setState({
				isIE: false,
			})
		} else {
			this.setState({
			  isIE: true,
			})
		}

		this.sliceData()    // 测试用
	}

    componentWillUnmount() {
		this.disconnectSocket()
	}

    // 左侧点击收起 or 展开
	treeFlex = () => {
        const { treeClose } = this.state;
		// 组织树伸缩
		if (treeClose === false) {
			this.itcLeftRef.style.display = 'flex';
			this.middleRef.style.width = '54%';
		} else {
			this.itcLeftRef.style.display = 'none';
			this.middleRef.style.width = '70%';
		}
		this.setState({
			treeClose: !treeClose,
		})
    }
    
	/** 获取组织树考勤节点 */
	getAtsPoint = point => {
		const { pointArr } = this.state;
		// 已经点过的考勤点将不会再次触发推送
		if(!pointArr.includes(point)) {
			this.setState({ point, pointArr: [...pointArr, point] }, () => {
				this.buildSocket(point)
			})
		}
    }
    
	buildSocket = (point) => {
		const { hostname } = window.location;
		const baseURL = isPro ? `http://${hostname}:14208` : 'http://192.168.109.161:14208';
		const urls = `${baseURL}/endpointAttendance`;
		console.log('serverUrl======::', urls, point)
		const hzSock = new HzSocket({
			wsUrl: urls,
			onConnectSuccess: this.handleSocketSuccess,
			topic: `/topic/snapface/${point}`,
			onMessage: this.handleMessage,
		})
		this.setState({
			socket: hzSock,
		})
	}

	handleSocketSuccess = () => {
		console.log('连接成功')
	}

	handleMessage = (res) => {
		const { totalData } = this.state
		console.log('推送数据', res)
		let totalPushData = [res, ...totalData]
		this.setState({
			totalData: totalPushData || [],
		}, () => {
			console.log('totalData---->', this.state.totalData)
		})
		this.sliceData()
	}

	// 关闭websocket连接
	disconnectSocket = () => {
		const { socket } = this.state
		if (socket && socket.close) {
			window.diconnetTimePassing = true;
			socket.close();
			this.setState({
				socket: null,
			})
		}
	}

	sliceData = () => {
        const { totalData } = this.state;
		this.setState({
			majorData: totalData.slice(0, 1),
			seconData: totalData.slice(1, 7),
		})
	}

    // 节点拖拽开始
	// dragStart = ({ event, node }) => {
	// 	this.node = node
	// 	this.setState({ dragFlag: true })
	// 	event.stopPropagation()
	// }
	
	// 关闭视频，修改videoData
	handleCloseEvent = (index = '', id) => {
		const { videoData, selectedDevice } = this.state;
		let data = [...videoData];
		if(index || index === 0) {
			// 删除已关闭的项
			data = data.filter((v,k) => v !== id);
			this.setState({
				videoData: data,
				currentItemValue: '',
				selectedDevice: id === selectedDevice ? '' : selectedDevice,
			})
		} else {
			// 关闭所有
			data = [];
			this.setState({
				videoData: data,
				currentItemValue: '',
				selectedDevice: '',
			})
		}
	};

	// 双击考勤点播放视频
	play = (obj) => {
		const { videoData, currentItemValue } = this.state;
		const { channel_code } = obj;
		let newVideoData = [...videoData];

		// 若当前选中的通道已经有视频，并且和当前点击的设备id不一致时，则要删除原来的id，播放新的视频id
		if(currentItemValue && currentItemValue !== channel_code) {
			newVideoData = newVideoData.filter(v => v !== currentItemValue);
		}
		// 判断当前点击的设备id是否已经在播放列表中，避免重复添加
		if(newVideoData.includes(channel_code)) {
			return message.info('此通道已在播放');
		}
		newVideoData.push(channel_code);
		this.setState({
			videoData: newVideoData,
			selectedDevice: channel_code,
		})
	}

	// 获取当前选中的目标id
	getCurrentItemValue = (v) => {
		this.setState({
			currentItemValue: v,
		})
	}

	// IE浏览器播放
	playIE = (obj) => {
		const { channel_code } = obj;
		VideIE.Play(channel_code);

		// 有分屏时执行这一步
		const currentWindowID = window.video.api.ocxUI.GetCurWndIndex();
		const { busyScreen } = this.state;
		busyScreen[currentWindowID] = true;
		this.setState({
			busyScreen: [
				...busyScreen,
			],
		})
	}

    render() {
		const { treeClose, majorData, seconData, selectedDevice, isIE } = this.state;

        return (
            <div className={styles.itcContent}>
                <div className={styles.itcLeft} ref={ref => {this.itcLeftRef = ref}}>
                    <LeftTree
                        hasBottom={true}
                        onDoubleClick={isIE ? this.playIE : this.play}
                        offDoubleClick={() => message.info("设备离线")}//离线设备添加双击事件
                        getAtsPoint={this.getAtsPoint}
                    />
                </div>
                <div className={treeClose ? styles.treeClose : styles.treeOpen} onClick={this.treeFlex} />
                <div className={styles.itcMiddle} ref={ref => {this.middleRef = ref}}>
                    <div className={styles.right}>
						{
							isIE ? (
								<VideIE />
							) : (
								<VideoWindow
									handleCloseEvent={this.handleCloseEvent}
									selectedDevice={selectedDevice}
									getCurrentItemValue={this.getCurrentItemValue}
								/>
							)
						}
                    </div>
                </div>
                <div className={styles.itcRight}>
                    <Record
                        majorData={majorData}
                        seconData={seconData}
                    />
                </div>
            </div>
        )
    }
}  