import React, { Component } from "react";
import moment from "moment";
import { message, Modal, Checkbox, Alert } from "antd";
import PropTypes from "prop-types";
import ToolBar from "./toolbar";
import { getUsername, getToken, toLoginPage, getOcxIp, getOcxConfig } from "./utils";
import ErrorDict from "./utils/video/lib/translateErrCode"
import {
	IC_ERROR_VERIFY_USERNOTEXIST,
	IC_ERROR_VERIFY_TIMEOUT,
	IC_ERROR_VERIFY_USERNOTREUSE,
} from "./utils/ocxError";
import video from "./utils/video";
import Cookies from 'js-cookie';
import styles from './index.less';

export default class VideoIE extends Component {
	static defaultProps = {
		isCloseAll: true, // 是否有关闭全部按钮
		isSplitScreen: true, // 是否有分屏按钮
		isFullScreen: true, // 是否有全屏显示按钮
		hideToolbarCode: [3, 4, 15], // 要隐藏的视频上方工具栏代码，通过 ID 隐藏视频窗中 toolbar 上的功能，具体 ID 值查看文档 【VideoMatrix-事件-工具栏按钮事件】
	}

	constructor() {
		super();
		this.state = {
			selectedWin: 0, // 被选中屏坐标
			videoWidth: 0, // 窗口宽度
			videoHeight: 0, // 窗口高度
			splitScreen: 1, // 屏数
			videoNumber: 0, // 视频播放数
			loadingData: [], // 下载数据
			downLoadedData: [], // 已下载数据
			downLoadDelData: [], // 已删除数据
			speed: Array.from({ length: 16 }).map(() => 0), // 视频播放速度
			stop: Array.from({ length: 16 }).map(() => 1), // 视频播放暂停
			frame: Array.from({ length: 16 }).map(() => 0), // 单帧播放
			isSupport: true, // 是否支持播放
			sound: Array.from({ length: 16 }).map(() => 0), // 声音
			soundAll: 1, // 全局声音
			playWindowObject: null,
			webICObject: null,
			progressObject: null,
			OnVideoPlay: Array.from({ length: 16 }).map(() => 0), // 是否进入播放环节了
			ocxConfig: {},
			streamMain: true,
		};
	}

	UNSAFE_componentWillMount() {
		video();
	}

	componentDidMount() {
		// 创建 OCX WebIC 控件、 Progressbar 和 VideoMatrix 播放窗口
		this.create();
		this.resizeHandler();
		getOcxConfig().then(ocxConfig => {
			this.setState({ ocxConfig }, () => {
				this.login();
			})
		})
	}

	componentDidUpdate(prevProps, prevState) {
		this.resizeHandler();
		this.getTreeHeight();
		if (
			prevProps.progressInfos !== this.props.progressInfos &&
			this.props.progressInfos
		) {
			// const {index, data} = this.processProgressInfo(this.props.progressInfo)
			this.initProgress(this.props.progressInfos);
		}
	}

	componentWillUnmount() {
		window.removeEventListener("resize", this.resizeHandler);
		window.removeEventListener("message", this.handleMsg);
	}

	/**
	 * 创建ocx控件窗口和控制
	 */
	create = () => {
		const { hideToolbarCode } = this.props;
		// *********** no return value in this block
		let container = this.containerRef;
		const { videoWidth, videoHeight } = this.getWinSize(container);
		// container.style.width = `${videoWidth}px`;
		// 创建 OCX 视频窗口
		const playWindowObject = window.video.createWebPlayWnd({
			container,
			style: `width:${videoWidth}px; height: ${videoHeight *
				0.6}px;display: block;`,
			classid: "clsid:E58DE9DD-6C88-42B9-AF3D-EC9AEEFF3630",
		});

		if (playWindowObject === null || playWindowObject === undefined) {
			return message.error("请下载最新版本ocx!", 3).then(() => {
				toLoginPage();
			});
		}
		// 创建 OCX 控制窗口
		const webICObject = window.video.createWebIC({
			container,
			style: "width: 0px;height: 0px;display: block;",
			classid: "clsid:215271FC-1AFA-4CB9-81F3-A44F7BA079C1",
		});
		this.setState({
			playWindowObject,
			webICObject,
		});

		// 创建 OCX 进度条
		if (this.props.enableProgress) {
			const progressObject = window.video.createProgross({
				container: this.containerRef,
				style: `width:100%; height: ${videoHeight * 0.4}px;display: block;`,
				classid: "clsid:aee53839-ccf8-42bb-929c-07b9dbf05bf4",
			});
			this.setState({
				progressObject,
			});
		}
		if (window.video.api.ocxContral.version) {
			// 通过 ID 隐藏视频窗中 toolbar 上的功能，具体 ID 值查看文档 【VideoMatrix-事件-工具栏按钮事件】
			for(let i=0; i < hideToolbarCode.length; i++ ) {
				window.video.api.hideToolbarButton(hideToolbarCode[i], true);
			}
		}
		this.setState({ videoWidth, videoHeight });
	};

	/**
	 * 登录平台
	 */
	login = async () => {
		const { ocxConfig } = this.state
		try {
			// 判断版本是否一样，不一样要求更新ocx版本
			if (!await window.video.api.checkOcx()) {
				this.setState({ isSupport: false });
				return false;
			}
			const ip = await getOcxIp()
			// const ip = '192.168.109.160'
			const port = ocxConfig.videoLogin.port
			const username = getUsername()
			const applicationUrl = decodeURIComponent(Cookies.get('application_url'))
			// const applicationUrl = decodeURIComponent('http://192.168.109.160:8018')
			const serverUrl = `${applicationUrl}/cas/validate`
			const ticket = await getToken({
				application_url: serverUrl,
				user_name: username,
			})
			window.video.api.Login(
				ip,
				port,
				username,
				ticket,
				serverUrl,
				result => {
					console.log("登录结果: ", JSON.stringify(result))
					if (result) {
						this.judgeUsers(result)
						try {
							this.addEvent();
						} catch (err) {
							window.alert("videoWindow eventInit error");
							console.log("videoWindow eventInit error", err);
						}
					}
				}
			);
			return true;
		} catch (err) {
			this.setState({ isSupport: false });
			console.log('err=====::', err);
			return false;
		}
	};

	/**
	 * 判断用户状态
	 */
	judgeUsers = (result) => {
		if (result.Error === IC_ERROR_VERIFY_USERNOTEXIST) {
			message.error("用户不存在");
		} else if (result.Error === IC_ERROR_VERIFY_TIMEOUT) {
			message.error("认证超时");
		} else if (result.Error === IC_ERROR_VERIFY_USERNOTREUSE) {
			message.error("用户不能复用");
		} else {
			if (this.props.afterLogin) {
				this.props.afterLogin();
			}
		}
	}

	/**
	 * 绑定事件
	 */
	addEvent() {
		const video = window.video;
		// 系统事件通知
		video.events.addVideoEvent("onEventNotify", json => {
			const { selectedWin, stop, OnVideoPlay, speed } = this.state;
			// console.log(JSON.stringify(stop, selectedWin))
			const temp = JSON.parse(json);
			console.log(temp.notify);
			if (temp.notify === "E_OCX_StreamStop") {
				stop[selectedWin] = 1;
				OnVideoPlay[selectedWin] = 0;
				speed[selectedWin] = 0;
				this.setState(
					{
						stop: [...stop],
						OnVideoPlay: [...OnVideoPlay],
						speed: [...speed],
					},
					() => {
						// console.log(999, JSON.stringify(this.state.stop))
					}
				);
			}
		});

		// 工具栏
		video.events.addVideoEvent("btnClick", (index, type) => {
			console.log('btnClick', index)
			// 关闭
			if (type === 9) {
				let puid = video.api.getPUIDByIndex(index), // 设备id
					progrossList = video.api.getProgressListByIndex(index), // 列表
					VODProgressInfo = video.api.getVODProgress(index); // 当前播放进度百分比

				// 播放控制按钮状态置为播放
				const { stop, selectedWin, OnVideoPlay, speed } = this.state;
				stop[selectedWin] = 1;
				OnVideoPlay[selectedWin] = 0;
				speed[selectedWin] = 0;
				this.setState({
					stop: [...stop],
					OnVideoPlay: [...OnVideoPlay],
					speed: [...speed],
				});

				video.api.CloseBtn(index, () => {
					this.props.closeVideo && this.props.closeVideo();
					// 录像
					if (this.props.type === "playback") {
						// 焦点
						this.setFocusIndex(index, puid);

						// 当前播放进度
						if (VODProgressInfo.Error === 0) {
							video.api.progrossInfo[progrossList.index].vodProgress =
								VODProgressInfo.Progress;
						}
					}

					// 暂停状态
					let state = {};
					this.initState(state);
					console.log(JSON.stringify(state));

					// 设置窗口数
					this.setState({
						...state,
						videoNumber: Object.keys(video.api.PdWndIndex).length,
					});
				});

				// 列表中去掉当前窗口puid
				video.api.clearPdWndIndex(index)
			}

			if (type === 10) {
				video.api.delLoopStream(index, () => {
					this.setState({
						videoNumber: Object.keys(video.api.PdWndIndex).length,
					});
				});
			}

			if (type === 20) {
				this.setState({
					streamMain: !this.state.streamMain,
				}, () => {
					video.api.SwitchStream(this.state.streamMain ? 0 : 1)
				})
			}
		});

		// 即时回放时间
		video.events.addVideoEvent("realTimePlayTimeChanged", (index, sec) => {
			video.api.SetInstantReplayDuration(index, sec);
		});

		// 电子放大(通过鼠标滚轮滚动)
		video.events.addVideoEvent("onWheelMouse", (index, x, y, delta) => {
			/* if (delta > 0) {
				 message.info(`放大`);
			 } else if (delta < 0) {
				 message.info(`缩小`);
			 } */
			video.api.ZoomInOut(x, y, delta, index);
		});

		// 轮播
		video.events.addVideoEvent("loopIntervalChanged", (index, sec) => {
			video.api.LoopStreamSetDuration(index, sec);
		});

		video.events.addVideoEvent(
			"focusChanged",
			(() => {
				return (index, flag) => {
					if (flag === 2) {
						// flag === 2 双击
						const {
							windowIndex,
							puid,
							key,
							alarmStartTime,
							alarmEndTime,
						} = this.props.progressInfos[index];
						const { stop } = this.state;
						stop[index] = 0;
						this.setState({
							selectedWin: index,
							stop: [...stop],
						});
						const hwnd = window.video.api.ocxUI.getHWnd(windowIndex);
						const BeginTime = alarmStartTime;
						const EndTime = alarmEndTime;
						window.video.api.PdWndIndex[key] = windowIndex;
						const params = JSON.stringify({
							hwnd,
							channel_code: puid,
							start: BeginTime,
							end: EndTime,
						});
						window.video.api.ocxContral.DoWebICFunction(
							"OssVodByTime",
							params,
							res => { },
							1
						);
					} else if (flag === 1) {
						// flag === 1 为单机
						window.video.api.ocxUI.SelectUnit(index);
						this.setState({
							selectedWin: index,
						});
					}
				};
			})()
		);
		video.events.addVideoEvent(
			"lockNofity",
			(index, flag, beginTime, endTime) => {
				if (
					!window.video.api.progross.setFileInfo(
						index,
						beginTime,
						endTime,
						1,
						0
					)
				) {
					message.info("录像锁定成功");
				}
			}
		);

		// 进度条帧点击事件
		video.events.addVideoEvent(
			"progressChanged",
			(index, flag, progressTime, progressFrame) => {
				const { ocxUI } = window.video.api;
				const { stop } = this.state;
				// 如果是暂停状态，先播放再设置进度
				if (stop[index] === 1) {
					const progressStatus = JSON.parse(
						video.api.ocxContral.getVODProgress(video.api.ocxUI.getHwnd(index))
					);
					if (video.api.getPUIDByIndex(index) && progressStatus.Error === 0) {
						// 播放中暂停
						video.api.OssSeek(ocxUI.getHWnd(index), progressTime)
					} else {
						// 未播放
						this.OSSVODResume(res => {
							if (res.Error === 0) {
								video.api.OssSeek(ocxUI.getHWnd(index), progressTime);
							}
						})
					}
				} else {
					video.api.OssSeek(ocxUI.getHWnd(index), progressTime);
				}
				stop[index] = 0;
				this.setState({
					stop: [...stop],
				});
			}
		);

		video.events.addVideoEvent("LMouseClient", wndIndex => {
			this.setState({
				selectedWin: wndIndex,
			});
			if (window.video.api.progross)
				window.video.api.progross.setFocusIndex(wndIndex);
		});

		// video.events.registPlaywndEvent("OnDropEvent",(res) => {
		//   console.log('OnDropEvent', res);
		// });

		window.addEventListener("message", this.handleMsg);

		// 自适应
		// window.onresize = () => {
		//     this.resizeHandler()
		// };
		// 播放成功后调用
		window.video.api.successPlayVideo.push((puid, index) => {
			let state = {};
			const setState = (index, name, val = 0) => {
				const tmpl = [...this.state[name]];
				tmpl[index] = val;
				return { [name]: [...tmpl] };
			};
			["speed", "stop", "frame", "sound"].forEach(item => {
				state = { ...state, ...setState(index, item) };
			});

			// 每次播放完成检测一遍
			this.initState(state);
			this.setState({
				...state,
				videoNumber: Object.keys(window.video.api.PdWndIndex).length,
			});
		});

		window.video.api.successSelectUnit.push(() => {
			this.setState({
				selectedWin: window.video.api.ocxUI.GetCurWndIndex(),
			});
		});
	}

	getTreeHeight = () => {
		window.addEventListener("resize", this.resizeHandler);
	};

	handleMsg = e => {
		const data = e.data;
		const api = window.video.api;
		if (data.type === "ocx") {
			if (data.action === "hide") {
				api.hide();
			} else if (data.action === "show") {
				api.show();
			}
		}
	};

	getWinSize = container => {
		let videoHeight = container.offsetHeight,
			videoWidth = container.offsetWidth;
		return { videoWidth, videoHeight };
	};

	resizeHandler = () => {
		const { playWindowObject, progressObject } = this.state;
		const { isCloseAll, isSplitScreen, isFullScreen } = this.props;
		const isToolBar = isCloseAll || isSplitScreen || isFullScreen;

		let container = this.containerRef;
		if (!container) return;
		const { videoWidth, videoHeight } = this.getWinSize(container);
		const Proportion = this.props.enableProgress ? 0.7 : 1;
		const TOOLBARHEIGHT = isToolBar ? 30 : 0;
		if (playWindowObject) {
			playWindowObject.style.cssText = `width:${videoWidth}px; height: ${(videoHeight -
				TOOLBARHEIGHT) *
				Proportion}px`;
		}
		if (this.toolbarRef) {
			this.toolbarRef.style.cssText = `width:${videoWidth}px; top: ${(videoHeight -
				TOOLBARHEIGHT) *
				Proportion}px; position: absolute;`;
		}
		if (progressObject) {
			progressObject.style.cssText = `width:${videoWidth}px; height: ${(videoHeight -
				TOOLBARHEIGHT) *
				(0.99 - Proportion)}px; top: ${TOOLBARHEIGHT}px;position: relative`;
		}
	};

	// 下载时间修改
	/* ChangeDownLoadRangTime = (row, name) => timeArr => {
		let time = `${moment(timeArr[0].unix() * 1000).format(
			"YYYY-MM-DD HH:mm:ss"
		)}~${moment(timeArr[1].unix() * 1000).format("YYYY-MM-DD HH:mm:ss")}`;
		const state = this.state[name];
		this.setState({
			[name]: state.map(item => {
				if (row.checkbox === item.checkbox && row.time === item.time) {
					return { ...item, time };
				} else {
					return item;
				}
			}),
		});
	}; */

	/**
	 * 初始化状态
	 */
	initState = (state = {}) => {
		let { splitScreen, stop } = this.state;
		let number = Math.max(splitScreen, stop.length);
		for (let i = 0; number > i; i++) {
			// if (!window.video.api.getPUIDByIndex(i)) {
			if (!state.stop) state.stop = [];
			state.stop[i] = 1;
			["speed", "frame", "sound", "OnVideoPlay"].forEach(item => {
				if (!state[item]) state[item] = [];
				state[item][i] = 0;
			});
			// }
		}
	};

	/**
	 * 焦点
	 */
	setFocusIndex = (index, id) => {
		let api = window.video.api,
			puid = id || api.getPUIDByIndex(index);
		if (puid != null) {
			let realpuid = puid.split("-")[0];
			api.setFocusIndex(realpuid, puid.replace(realpuid + "-", ""));
		} else {
			// 选中第一个未播放
			for (let index in api.progrossInfo) {
				let info = api.progrossInfo[index],
					unPlay = true;
				for (let key in api.PdWndIndex) {
					if (info.puid + "-" + info.date === key) {
						unPlay = false;
						break;
					}
				}
				if (unPlay) {
					api.progross.setFocusIndex(index);
					break;
				}
			}
		}
	};

	/**
	 * 分屏
	 */
	changeScreen = index => () => {
		this.setState({ splitScreen: index });
		window.video.api.createWinNum(index);
	};

	/**
	 * 快进
	 */
	OSSSetSpeed = value => () => {
		const { speed, selectedWin } = this.state;
		speed[selectedWin] = value;
		this.setState({
			speed: [...speed],
		});
		window.video.api.OSSSetSpeed(value);
	};

	/**
	 * 暂停
	 */
	OSSVODPause = () => {
		const { stop, selectedWin } = this.state;
		const tmpl = [...stop];
		tmpl[selectedWin] = 1;
		this.setState({ stop: [...tmpl] });
		window.video.api.OSSVODPause();
	};

	/*按时间段点播平台录像*/
	OSSVODResume = (callback) => {
		const { stop, selectedWin, OnVideoPlay } = this.state,
			api = window.video.api;
		stop[selectedWin] = 0;
		this.setState({
			stop: [...stop],
		});
		// 展示
		window.video.api.show();
		const progressStatus = JSON.parse(
			api.ocxContral.getVODProgress(api.ocxUI.getHwnd(selectedWin))
		);
		if (api.getPUIDByIndex(selectedWin) && progressStatus.Error === 0) {
			api.OSSVODResume(callback);
		} else {
			OnVideoPlay[selectedWin] = 1;
			this.setState({
				OnVideoPlay: [...OnVideoPlay],
			});
			let index = api.progross.getFocus();
			if (index !== -1) {
				const { puid, alarmStartTime, alarmEndTime } = this.props.progressInfos[
					index
				];
				const hwnd = window.video.api.ocxUI.getHWnd(index);
				const params = JSON.stringify({
					hwnd,
					channel_code: puid,
					start: alarmStartTime,
					end: alarmEndTime,
				});
				window.video.api.ocxContral.DoWebICFunction(
					"OssVodByTime",
					params,
					res => {
						const result = JSON.parse(res);
						const errorCode = result.Error
						if (result.Error !== 0) {
							if (ErrorDict[errorCode]) {
								message.error(ErrorDict[errorCode]);
							} else {
								message.error(`ocx错误码: ${errorCode}`);
							}
						}
					},
					1
				);
			}
		}
	};

	//单帧播放
	PlayOneByOne = index => {
		const { stop, selectedWin, frame } = this.state;
		const hwnd = window.video.api.ocxUI.getHWnd(index);
		console.log(stop, selectedWin, frame);
		stop[selectedWin] = 0;
		frame[selectedWin] = 1;
		this.setState({
			stop: [...stop],
			frame: [...frame],
		});
		window.video.api.PlayOneByOne(hwnd, 1);
	};

	//关闭单帧播放
	ResumePlayByFrame = index => {
		const hwnd = window.video.api.ocxUI.getHWnd(index);
		const { selectedWin, frame } = this.state;
		const tmpl = [...frame];
		tmpl[selectedWin] = 0;
		this.setState({ frame: [...tmpl] });
		window.video.api.ResumePlayByFrame(hwnd);
	};

	/**
	 * 关闭视频
	 */
	closeVideo = () => {
		let video = window.video,
			colseNumber = 0,
			len = Object.keys(video.api.PdWndIndex).length;
		for (let id in video.api.PdWndIndex) {
			let index = video.api.PdWndIndex[id],
				VODProgressInfo = video.api.getVODProgress(index),
				progrossList = video.api.getProgressListByIndex(index);
			// 当前播放进度
			if (VODProgressInfo.Error === 0) {
				video.api.progrossInfo[progrossList.index].vodProgress =
					VODProgressInfo.Progress;
			}
			// eslint-disable-next-line no-loop-func
			video.api.CloseBtn(index, () => {
				colseNumber++;
				if (colseNumber === len) {
					this.props.closeVideo && this.props.closeVideo();
					let state = {};
					this.initState(state);
					this.setState({ ...state, videoNumber: 0 });
				}
			});
		}
	};

	/** 关闭全部分屏 */
	closeAllVideo = () => {
		// *********** no return value in this block
		const video = window.video;
		for (let index = 0; index < 16; index++) {
			if (video.api.isLock(index)) return message.info('当前窗口无法关闭')
			video.api.CloseBtn(index, () => {
			});
		}
		video.api.clearAllPdWndIndex()
	}

	/**
	 * 开声音
	 */
	openSound = () => {
		const { sound } = this.state;
		sound.forEach((item, index) => {
			if (typeof item !== "undefined" && item === 1) {
				window.video.api.OpenSound(index);
			}
		});
		this.setState({ soundAll: 1 });
	};

	/**
	 * 关声音
	 */
	closeSound = () => {
		const { sound } = this.state;
		sound.forEach((item, index) => {
			if (typeof item !== "undefined") {
				window.video.api.CloseSound(index);
			}
		});
		this.setState({ soundAll: 0 });
	};

	TimeoutProgress = e => {
		let stateValueArr = null;
		const callTime = () => {
			const loadingDatas = this.getDownloadData();
			// *********** no return value in this block
			if (loadingDatas !== null && loadingDatas !== undefined) {
				stateValueArr = loadingDatas.forEach(item => {
					if (item.STATUS === 1 && item.Progress > 0 && item.Progress < 1) {
						return item.Progress;
					}
				});
			}
		};
		const interval = setInterval(callTime, 1000);
		if (stateValueArr.length === 0 || e === 1) {
			//需要修改  原因是 stateValueArr 无法拿到值
			clearInterval(interval);
		}
	};

	// 获取所有下载资源
	getDownloadData = () => {
		let api = window.video.api,
			index = api.progross.getFocus(),
			info = api.progrossInfo[index],
			data = api.ocxContral.OSSFileDLStatus(),
			FileDLStatus = JSON.parse(data || "{}").FileDLStatus || [],
			downLoadedData = [],
			downLoadDelData = [],
			loadingData = [];
		function setdownload(state, status) {
			let data = [...state];
			FileDLStatus.forEach(infos => {
				if (infos.STATUS === status) {
					let time = infos.DLPATH.replace(/.*\/(.*)\.mp4$/, "$1");
					time = time.split("-");
					time = `${moment(time[0], "YYYYMMDDHHmmss").format(
						"YYYY-MM-DD HH:mm:ss"
					)}~${moment(time[1], "YYYYMMDDHHmmss").format(
						"YYYY-MM-DD HH:mm:ss"
					)}`;
					data = [
						...data,
						{
							checkbox: infos.PUID,
							status: infos.STATUS,
							point: infos.PUNAME,
							time,
							progress: infos.Progress,
							speed: 0,
							path: infos.DLPATH,
							channel: infos.channel,
						},
					];
				}
			});
			return data;
		}

		// 下载中资源
		loadingData = setdownload(loadingData, 1);
		loadingData = setdownload(loadingData, 2);
		loadingData = setdownload(loadingData, 3);

		if (index !== -1) {
			const BeginTime = info.fileInfo.File[0].BeginTime;
			const EndTime = info.fileInfo.File[info.fileInfo.File.length - 1].EndTime;
			let time = `${moment(BeginTime * 1000).format(
				"YYYY-MM-DD HH:mm:ss"
			)}~${moment(EndTime * 1000).format("YYYY-MM-DD HH:mm:ss")}`;
			let some = loadingData.some(item => {
				return item.checkbox === info.puid && item.status === 0;
			});
			if (!some) {
				loadingData = [
					...loadingData,
					{
						checkbox: info.puid,
						puid: info.key,
						status: 0,
						point: info.name,
						time,
						progress: 0,
						speed: 0,
					},
				];
			}
		}
		// 下载中资源
		// loadingData = setdownload(loadingData, 1);
		// loadingData = setdownload(loadingData, 2);
		// loadingData = setdownload(loadingData, 3);

		this.setState({ loadingData });

		// 下载完成资源
		downLoadedData = setdownload(downLoadedData, 4);
		this.setState({ downLoadedData });

		// 删除
		downLoadDelData = setdownload(downLoadDelData, 5);
		this.setState({ downLoadDelData });

		return loadingData;
	};

	// 开始下载
	loadDowning = checkedData => () => {
		const { loadingData } = this.state;
		this.setState({
			loadingData: loadingData.map(item => {
				let time = item.time;
				let splitTime = time.split("~");
				let startTime = moment(splitTime[0], "YYYY-MM-DD HH:mm:ss").format(
					"YYYYMMDDHHmmss"
				),
					endTime = moment(splitTime[1], "YYYY-MM-DD HH:mm:ss").format(
						"YYYYMMDDHHmmss"
					);
				let ok = checkedData.some(checked => {
					return (
						checked.checkbox === item.checkbox && checked.time === item.time
					);
				});
				if (ok) {
					if (item.status === 0) {
						this.TimeoutProgress();
						window.video.api.OSSFileDLStart(
							item.puid,
							startTime,
							endTime,
							item.point
						);
						return { ...item, status: 1 };
					}
					if (item.status === 2) {
						this.TimeoutProgress();
						window.video.api.OSSFileDLResume(
							item.checkbox,
							item.channel,
							startTime,
							endTime,
							item.point
						);
						return { ...item, status: 1 };
					}
				}
				return { ...item };
			}),
		});
	};

	// 清除
	OSSFileDLDel = fileArr => () => {
		fileArr.forEach(item => {
			let time = item.time,
				splitTime = time.split("~"),
				startTime = moment(splitTime[0], "YYYY-MM-DD HH:mm:ss").format(
					"YYYYMMDDHHmmss"
				),
				endTime = moment(splitTime[1], "YYYY-MM-DD HH:mm:ss").format(
					"YYYYMMDDHHmmss"
				);
			window.video.api.OSSFileDLDelete(
				item.checkbox,
				item.channel,
				startTime,
				endTime,
				2
			);
		});
		this.setState({
			downLoadDelData: this.state.downLoadDelData.filter(item => {
				return !fileArr.some(file => {
					return file.checkbox === item.checkbox && file.time === item.time;
				});
			}),
		});
	};

	// 删除
	OSSFileDLDelete = (checkedData, name) => () => {
		let ok = false;
		Modal.confirm({
			title: "是否删除?",
			content: (
				<Checkbox
					onChange={e => {
						ok = e.target.checked;
						// ok
					}}
				>
					同时删除文件
				</Checkbox>
			),
			onOk: () => {
				this.setState({
					[name]: this.state[name].filter(item => {
						let isCheck = checkedData.some(checked => {
							return (
								checked.checkbox === item.checkbox && checked.time === item.time
							);
						});
						if (isCheck) {
							let time = item.time;
							let splitTime = time.split("~");
							let startTime = moment(
								splitTime[0],
								"YYYY-MM-DD HH:mm:ss"
							).format("YYYYMMDDHHmmss"),
								endTime = moment(splitTime[1], "YYYY-MM-DD HH:mm:ss").format(
									"YYYYMMDDHHmmss"
								);
							window.video.api.OSSFileDLDelete(
								item.checkbox,
								item.channel,
								startTime,
								endTime,
								ok
							);
							this.setState({
								downLoadDelData: [
									...this.state.downLoadDelData,
									{ ...item, status: 5 },
								],
							});
						}
						return !isCheck;
					}),
				});
				ok = false;
			},
			onCancel() {
				ok = false;
			},
		});
	};

	// 暂停
	OSSFileDLPause = checkedData => () => {
		const { loadingData } = this.state;
		this.setState({
			loadingData: loadingData.map(item => {
				let ok = checkedData.some(checked => {
					return (
						checked.checkbox === item.checkbox && checked.time === item.time
					);
				});
				if (item.status === 1 && ok) {
					let time = item.time;
					let splitTime = time.split("~");
					let startTime = moment(splitTime[0], "YYYY-MM-DD HH:mm:ss").format(
						"YYYYMMDDHHmmss"
					),
						endTime = moment(splitTime[1], "YYYY-MM-DD HH:mm:ss").format(
							"YYYYMMDDHHmmss"
						);
					window.video.api.OSSFileDLPause(
						item.checkbox,
						item.channel,
						startTime,
						endTime
					);
					this.TimeoutProgress(1);
					return { ...item, status: 2 };
				} else {
					return { ...item };
				}
			}),
		});
	};

	initProgress = progressInfos => {
		const api = window.video.api;
		api.progrossInfo = progressInfos;
		progressInfos.forEach((progressInfo, index) => {
			const {
				windowIndex,
				fileInfo,
				searchDate,
				name,
				alarmStartTime,
				alarmEndTime,
			} = progressInfo;
			api.PdWndIndex[progressInfo.key] = index;
			window.video.api.setLabel(
				windowIndex,
				name,
				searchDate.format("YYYY-MM-DD")
			);
			window.video.api.setFileInfo(
				windowIndex,
				fileInfo,
				alarmStartTime,
				alarmEndTime
			);
			api.bindProgress(windowIndex);
		});
	};

	dragOver = e => {
		e.preventDefault();
	};

	dragEnter = e => {
		e.preventDefault();
	};

	drop = e => {
		e.preventDefault();
	};

	render() {
		const {
			isCloseAll,
			isSplitScreen,
			isFullScreen,
		} = this.props;

		const {
			videoWidth,
			splitScreen,
			speed,
			selectedWin,
			stop,
			OnVideoPlay,
			frame,
			isSupport,
			ocxConfig,
		} = this.state;

		if (!isSupport) {
			return (
				<Modal
					title=""
					visible={true}
					footer=""
					closable={false}
					width={500}
					height={500}
					maskStyle={{ top: "64px" }}
					wrapClassName="ocxModal"
				>
					<Alert
						type="warning"
						banner
						message={
							<span>
								请下载安装最新视频播放控件
                <a href={`${ocxConfig.ocxUrl}`} target="_blank" rel="noreferrer">
									点击下载
                </a>
								<div>
									本地安装版本：{window.video.api.ocxContral.version ? window.video.api.ocxContral.version() : `未安装ocx`}
								</div>
								<div>
									服务器提供版本：{ocxConfig.ocxVersion}
								</div>
							</span>
						}
					/>
				</Modal>
			);
		}

		const isToolBar = isCloseAll || isSplitScreen || isFullScreen;
		return (
			<div
				id="windowVideo"
				ref="windowVideo"
				style={{
					width: "100%",
					height: "100%",
				}}
				onDrop={this.drop}
				onDragOver={this.dragOver}
				onDragEnter={this.dragEnter}
			>
				<div className={styles.videoWindow}>
					{/* 未播放视频时显示背景 */}
					{/* {videoNumber === 0 ? (
            <div className="background">
              <div />
            </div>
          ) : (
            ""
          )} */}
					{/*视频容器*/}
					<div ref={ref => this.containerRef = ref} className={styles.container}>
						<div ref={ref => this.toolbarRef = ref}>
							{isToolBar && (
								<ToolBar
									isCloseAll={isCloseAll}
									isSplitScreen={isSplitScreen}
									isFullScreen={isFullScreen}
									videoWidth={videoWidth}
									splitScreen={splitScreen}
									changeScreen={this.changeScreen}
									closeVideo={this.closeVideo}
									type={this.props.type}
									speed={speed[selectedWin]}
									OSSSetSpeed={this.OSSSetSpeed}
									stop={stop[selectedWin]}
									OnVideoPlay={OnVideoPlay[selectedWin]}
									OSSVODPause={this.OSSVODPause}
									OSSVODResume={this.OSSVODResume}
									frame={frame[selectedWin]}
									PlayOneByOne={this.PlayOneByOne}
									ResumePlayByFrame={this.ResumePlayByFrame}
									openSound={this.openSound}
									closeSound={this.closeSound}
									// sound={sound[selectedWin]}
									soundAll={this.state.soundAll}
									loadingData={this.state.loadingData}
									getDownloadData={this.getDownloadData}
									loadDowning={this.loadDowning}
									downLoadedData={this.state.downLoadedData}
									downLoadDelData={this.state.downLoadDelData}
									ChangeDownLoadRangTime={this.ChangeDownLoadRangTime}
									OSSFileDLPause={this.OSSFileDLPause}
									OSSFileDLDelete={this.OSSFileDLDelete}
									OSSFileDLDel={this.OSSFileDLDel}
									closeAllVideo={this.closeAllVideo}
								/>
							)}
						</div>
					</div>
				</div>
			</div>
		);
	}
}

VideoIE.propTypes = {
	enableProgress: PropTypes.bool,
	progressInfos: PropTypes.array,
	progressFirstPlay: PropTypes.func,
	progressSelectPlay: PropTypes.func,
	afterLogin: PropTypes.func,
};

VideoIE.Play = (key) => {
	if(window.video) {
		const {
			api,
		} = window.video;
		const currentWindowID = window.video.api.ocxUI.GetCurWndIndex();

		api.forceCreateRTVideo(key, 0, (e) => {
			if (!e) {
				api.SelectUnit(currentWindowID);
			}
		}, currentWindowID, key);
	}
}
