/* eslint-disable */
import { message } from "antd";
import React from "react";
import { getOcxConfig } from "../../../utils";
import ErrorDict from './translateErrCode'

export default class OCXApi {
	constructor() {
		const video = window.video;
		const asyncEvents = video.utils.asyncEvents;
		this.ocxUI = null; // UI窗口
		this.ocxContral = null; // 控制窗口
		this.progross = null; // 进度条
		this.asyncEvents = {
			on: asyncEvents.on.bind(asyncEvents),
		}; // 异步事件绑定 一定要这么绕？
		this.isLogin = false;
		// this.reLogin = () => {};
		this.successPlayVideo = [];
		this.successDeleteVideo = [];
		this.successSelectUnit = [];
		this.windowNumber = 0; // 已创建窗口数
		this.PdWndIndex = {}; // 设备id和窗口索引
		this.progrossPids = {};
		this.progrossInfo = {};
		this.maxWinNumber = 16;
		this.saveTime = []; //存录像开始时间
		this.windowState = "none"; //全局变量 控制confirm
		this.dataTree = [];
		this.windowLocks = [];
	}

	// 设置时间间隔
	SetInstantReplayDuration(index, sec = 5) {
		const time = Math.max(sec, 5); //最小5秒
		this.ocxContral.SetInstantReplayDuration(this.ocxUI.GetHWnd(index), time);
	}

	/**
	   * WebPlayWnd事件回调注册
	   */
	registPlaywndEvent = (funcName) => new Promise((resolve, rej) => {
		if (this.ocxUI) {
			console.log('this.ocxUI', this.ocxUI)
			this.ocxUI.registPlaywndEvent(funcName, (res) => {
				if (res) {
					const data = JSON.parse(res)
					resolve(data)
				} else {
					rej(res)
				}
			})
		}
	})

	/**
	 *
	 * @param {*} index
	 * @param sec
	 * @param callback
	 */
	InstantReplay(index, sec = 5, callback) {
		message.success(`即时回放${sec}秒。`);
		this.ocxContral.InstantReplay(this.ocxUI.GetHWnd(index));
	}

	/**
	 *
	 * @param ip
	 * @param port
	 * @param username
	 * @param ticket
	 * @param serverurl
	 * @param callback
	 * @description 登录
	 */
	Login(ip, port, username, ticket, serverurl, callback = () => { }) {
		this.windowState = "block";
		try {
			const JSONParams = JSON.stringify({
				ip,
				port,
				username,
				ticket,
				serverurl,
			})
			this.DoWebICFunction('LoginToken', JSONParams, result => {
				const resData = JSON.parse(result)
				if (resData.Error === 0) {
					this.isLogin = true;
					callback && callback(resData)
				} else {
					callback(resData);
				}
			})
		} catch (e) {
			// todo
		}
	}

	/**
	 * 刷新组织节点
	 * @param callback
	 */
	refreshOrgList(callback) {
		// 获取的是组织节点树
		// const queryId = this.ocxContral.QueryOrgList();
		// this.asyncEvents.on(queryId, result => {
		//   if (result && result.Error !== 0) {
		//     return message.info("资源加载中，请勿频繁切换");
		//   }
		//
		//   this.dataTree = result.resourceTree;
		//   callback();
		// });
		// this.DoWebICFunction("QueryOrgList", '{}', res => {
		//   const result = JSON.parse(res);
		//   if (result && result.Error !== 0) {
		//     return message.info("资源加载中，请勿频繁切换");
		//   }
		//   this.dataTree = result.resourceTree;
		//   callback();
		// });
	}

	/**
	 * @deprecated 退出登录
	 */
	Logout() {
		return this.ocxContral.Logout();
	}

	//停止即时回放
	InstantStop(index, callback) {
		const Inedx = this.ocxUI.GetHWnd(index);
		return this.ocxContral.InstantStop(Inedx);
	}

	getPUIDByIndex(index) {
		let puid = null;
		for (const key in this.PdWndIndex) {
			if (this.PdWndIndex[key] === index) {
				puid = key;
			}
		}
		return puid;
	}

	clearPdWndIndex = (index) => {
		const puid = this.getPUIDByIndex(index)
		delete this.PdWndIndex[puid]
	}

	clearAllPdWndIndex = () => {
		this.PdWndIndex = {}
	}

	/**
	 * @description 创建窗口
	 * @param {*} number
	 */
	createWinNum(number) {
		let success = -1;
		const num = Math.ceil(Math.sqrt(number));
		if (number >= 0) {
			success = this.ocxUI.ChangeWndNum(num - 1);
		}
		if (success === 0) {
			this.windowNumber = Math.pow(num, 2);
			return true;
		}
		return false;
	}

	/**
	 *
	 * @description 删除视频，清除设备id和窗口索引关系。
	 * @param ChannelCode 设备id
	 * @param callback
	 */
	_deleteVideo(ChannelCode, callback = () => { }) {
		const index = this.PdWndIndex[ChannelCode];
		let success = false;
		if (typeof index !== "undefined") {
			const jsonParams = {
				hwnd: this.ocxUI.GetHWnd(index),
			};
			this.DoWebICFunction(
				"StopStream",
				JSON.stringify(jsonParams),
				res => {
					const response = JSON.parse(res).Error;
					success = response === 0;
					if (success) {
						// if (index === this.PdWndIndex[ChannelCode]) {
						//   delete this.PdWndIndex[ChannelCode];
						// }
						callback(index, this.PdWndIndex);
					}
				},
				0
			);
		}
		return success;
	}

	// 框选最近的屏
	SelectUnit(index) {
		const flag = Math.floor(index / this.windowNumber); // 0

		let startIndex = this.windowNumber * flag; // 0
		if (this.windowNumber * (flag + 1) > 16) {
			//
			startIndex = 16 - this.windowNumber;
		}
		const nextIndexArr = [];
		let nextIndex;
		for (let i = 0; this.windowNumber > i; i++) {
			const index = startIndex + i;
			const puid = this.getPUIDByIndex(index);
			if (!puid) {
				nextIndexArr.push(index);
			}
		}
		if (nextIndexArr.length === 0) {
			if (index - startIndex + 1 === this.windowNumber) {
				nextIndex = startIndex;
			} else {
				nextIndex = index + 1;
			}
		} else {
			nextIndex = nextIndexArr[0];
			for (let i = 0; nextIndexArr.length > i; i++) {
				if (nextIndexArr[i] > index) {
					nextIndex = nextIndexArr[i];
					break;
				}
			}
		}
		this.ocxUI.SelectUnit(nextIndex);
		this.successSelectUnit.forEach(fn => {
			if (typeof fn === "function") {
				fn(nextIndex);
			}
		});
	}

	// 异步获取指定组织及全部下级通道信息
	QueryOrgSubChannels(id, callback) {
		if (this.isLogin) {
			// this.asyncEvents.on(this.ocxContral.QueryOrgSubChannels(id), result => {
			//   if (result && result.Error !== 0) {
			//     return message.info("获取设备信息失败");
			//   }
			//   callback && callback(result);
			// });
			const jsonParams = {
				org_id: id,
			};
			this.DoWebICFunction(
				"QueryOrgSubChannels",
				JSON.stringify(jsonParams),
				res => {
					const result = JSON.parse(res);
					if (result && result.Error !== 0) {
						return message.info("获取设备信息失败");
					}
					callback && callback(result);
				},
				1
			);
		} else {
			setTimeout(() => {
				this.QueryOrgSubChannels();
			}, 200);
		}
	}

	// 异步获取指定组织下级设备通道信息
	QueryOrgChannels(id, callback) {
		if (this.isLogin) {
			let data = [];
			let data2 = [];
			const params = {
				org_id: id,
				offset: 0,
				count: -1,
			};
			this.DoWebICFunction(
				"QueryOrgChannels",
				JSON.stringify(params),
				result => {
					const res = JSON.parse(result);
					data = res.channels.filter(function (item) {
						return (
							item.type === "ENC" ||
							item.type === "BAYONET" ||
							item.type === "ALARM"
						);
					});

					/*过滤摄像头类型 camera_type */
					data2 = data.map(item => {
						// 如果 type 是 ENC 或 BAYONET 并且存在 children
						if (
							(item.type === "ENC" || item.type === "BAYONET") &&
							item.children &&
							item.children.length
						) {
							// 过滤 children 的 camera_type
							item.children = item.children.filter(camera => {
								return (
									camera.camera_type === 1 ||
									camera.camera_type === 2 ||
									camera.camera_type === 3
								);
							});
							return item;
						} else if (
							item.type === "ALARM" &&
							item.children &&
							item.children.length
						) {
							// 因为报警主机 camera_type 不固定，所以用 设备类型来区分。
							item.children = item.children.map(camera => {
								camera.parentType = item.type;
								return camera;
							});
							return item;
						}
					});
					callback && callback(data2);
				}
			);
		} else {
			setTimeout(() => {
				this.QueryOrgChannels();
			}, 200);
		}
	}

	// 异步获取指定通道模糊匹配信息（返回包含设备和通道）
	QueryChannelsByName(val, callback) {
		if (this.isLogin) {
			let data = [];
			let data2 = [];
			this.asyncEvents.on(
				this.ocxContral.QueryChannelsByName(val, 0, -1),
				result => {
					if (result && result.Error !== 0) {
						return message.info("查询关键字失败");
					}
					data = result.channels.filter(function (item) {
						return (
							item.type === "ENC" ||
							item.type === "BAYONET" ||
							item.type === "ALARM"
						);
					});

					data2 = data.map(item => {
						// 如果 type 是 ENC 或 BAYONET 并且存在 children
						if (
							(item.type === "ENC" || item.type === "BAYONET") &&
							item.children &&
							item.children.length
						) {
							// 过滤 children 的 camera_type
							item.children = item.children.filter(camera => {
								return (
									camera.camera_type === 1 ||
									camera.camera_type === 2 ||
									camera.camera_type === 3
								);
							});
							return item;
						} else if (
							item.type === "ALARM" &&
							item.children &&
							item.children.length
						) {
							// 因为报警主机 camera_type 不固定，所以用 设备类型来区分。
							item.children = item.children.map(camera => {
								camera.parentType = item.type;
								return camera;
							});
							return item;
						}
					});
					callback && callback(data2);
				}
			);
		} else {
			setTimeout(() => {
				this.QueryChannelsByName();
			}, 200);
		}
	}

	/**
	 *
	 * @param {*} pids           设备id
	 * @param {*} channelIndex   通道
	 * @param {*} streamType     流类型
	 * @param callback
	 * @description      轮播播放
	 */
	loopStreamAdd(pids, channelIndex, streamType, callback) {
		if (Array.isArray(pids)) {
			const videoNum = Object.keys(this.PdWndIndex).length;
			let success = false;
			let key = pids.join("/"); // 是否播放录像
			if (this.windowNumber === 0) {
				this.createWinNum(1);
			}
			if (typeof this.PdWndIndex[key] !== "undefined") {
				success = this.delLoopStream(this.PdWndIndex[key]);
			} else {
				if (this.windowNumber <= videoNum) {
					let puid = Object.keys(this.PdWndIndex)[0];
					if (puid.indexOf("/") >= 0) {
						success = this.delLoopStream(0);
					} else {
						success = this._deleteVideo(puid);
					}
				} else {
					success = true;
				}
			}
			if (success) {
				let windowValue = this.ocxUI.GetCurWndIndex();
				let forkPuNumber = 0;
				let id = this.getPUIDByIndex(windowValue);
				let isOk = true;
				if (id) {
					if (id.indexOf("/") >= 0) {
						isOk = this.delLoopStream(windowValue);
					} else {
						isOk = this._deleteVideo(id);
					}
				}
				if (isOk) {
					pids.forEach(pid => {
						forkPuNumber++;
						// 所有设备构建完成
						if (forkPuNumber === pids.length) {
							pids.forEach(p => {
								this.ocxContral.LoopStreamAdd(
									this.ocxUI.GetHWnd(windowValue),
									p,
									channelIndex,
									streamType
								);
							});
							this.PdWndIndex[key] = windowValue;
							callback();
							this.SelectUnit(windowValue);
							this.successPlayVideo.forEach(fn => {
								if (typeof fn === "function") {
									fn(pids, windowValue);
								}
							});
						}
					});
				}
			}
		}
	}

	/**
	 *
	 * @param {*} index 窗口坐标
	 * @param callback
	 * @description 轮播关闭
	 */
	delLoopStream(index, callback = () => { }) {
		let pidsStr = this.getPUIDByIndex(index);
		let pids;
		if (pidsStr) {
			pids = pidsStr.split("/");
			if (pids.length > 1) {
				let ok = !pids.some(pid => {
					return (
						this.ocxContral.LoopStreamDel(this.ocxUI.GetHWnd(index), pid) !== 0
					);
				});
				if (ok) {
					return this._deleteVideo(pidsStr, callback);
				}
				return ok;
			} else {
				return false;
			}
		}
	}

	/**
	 *
	 * @param {*} index
	 * @param {*} nDuration
	 * @description  轮播时间设置
	 */
	LoopStreamSetDuration(index, nDuration) {
		this.ocxContral.LoopStreamSetDuration(this.ocxUI.GetHWnd(index), nDuration);
	}

	/**
	 *
	 * @param {*} channelCode
	 * @param {*} streamType
	 * @param callback
	 * @description
	 */
	forceCreateRTVideo(channelCode, streamType, callback, windowIndex) {
		if (!this.isLogin) {
			return message.info(`平台可能正在登陆，请稍后重试或刷新页面。`);
		}
		let puid = "";
		// let data = [];
		let isOnline = "";
		const createOneRTVideo = channelCode => {
			const videoNum = Object.keys(this.PdWndIndex).length;
			puid = channelCode;
			if (typeof puid !== "undefined") {
				let successDel = false;
				if (this.windowNumber === 0) {
					this.createWinNum(1);
				}
				// 已播放设备,删除
				if (typeof this.PdWndIndex[channelCode] !== "undefined") {
					message.info('此通道已在播放')
					successDel = true;
					return
				} else {
					// 已满屏，删除第一个
					if (this.windowNumber <= videoNum) {
						let puid = Object.keys(this.PdWndIndex)[0];
						if (puid.indexOf("/") >= 0) {
							successDel = this.delLoopStream(0);
						} else {
							successDel = this._deleteVideo(puid);
						}
					} else {
						successDel = true;
					}
				}
				// 当前窗口若有已播放的通道，则先清空当前通道
				let windowValue = this.ocxUI.GetCurWndIndex();
				let Id = this.getPUIDByIndex(windowValue), isOk = true;
				if (Id) {
					if (Id.indexOf('/') >= 0) {
						isOk = this.delLoopStream(windowValue);
					} else {
						isOk = this._deleteVideo(Id);
						delete this.PdWndIndex[Id]
					}
				}
				if (successDel) {
					let windowValue = this.ocxUI.GetCurWndIndex();
					let id = this.getPUIDByIndex(windowValue);
					let isOk = true;
					if (id) {
						if (id.indexOf("/") >= 0) {
							isOk = this.delLoopStream(windowValue);
						} else {
							isOk = this._deleteVideo(id);
						}
					}
					if (isOk) {
						const hwnd = this.ocxUI.GetHWnd(windowValue);
						const jsonParams = {
							// channel_code: data[2],
							channel_code: channelCode,
							stream: streamType,
							hwnd: hwnd,
						};
						// const hide = message.loading("连接到视频流", 0);
						this.DoWebICFunction(
							"StartStream",
							JSON.stringify(jsonParams),
							res => {
								console.log('res----->', res)
								message.destroy();
								const errorCode = JSON.parse(res).Error;
								if (errorCode === 0) {
									window.video.api.show();
									this.PdWndIndex[channelCode] = windowValue;
									callback(true);
								} else {
									callback(false);
									if (errorCode === 2012) {
										if (isOnline === "0" || isOnline === "2") {
											message.error("当前通道离线");
										} else {
											message.error('此通道不支持预览');
										}
										// bugfix 20201009
									} else if (errorCode === 2008) {
										message.error('此通道不支持预览');
									} else {
										if (ErrorDict[errorCode]) {
											message.error(ErrorDict[errorCode]);
										} else {
											message.error(`ocx错误码: ${errorCode}`);
										}
									}
								}
							},
							1
						);
					}
				}
			} else {
				callback();
			}
		};
		createOneRTVideo(channelCode);
	}

	/**
	 *
	 * @param {*} index    标志
	 * @param {*} path     路径
	 * @description        本地抓图
	 */
	CapturePic(index, path) {
		const success = this.ocxContral.CapturePic(this.ocxUI.GetHWnd(index), path);
		if (success === 0) {
			this.windowState = "block";
			message.success(
				<span>
					抓图成功！
          <a
						onClick={() => {
							this.ocxContral.GotoDir(path);
						}}
					>
						打开文件所在目录
          </a>
				</span>
			);
		} else {
			this.windowState = "none";
			message.info(<span>抓图失败！</span>);
		}
	}

	/**
	 *
	 * @param {*} index
	 * @param {*} path
	 * @param {*} maxFileTime
	 * @description           本地视频下载
	 */
	StartRecordLocal(index, path, maxFileTime) {
		this.ocxContral.recordLocalpath = path;
		this.ocxContral.StartRecordLocal(
			this.ocxUI.GetHWnd(index),
			path,
			maxFileTime
		);
	}

	/**
	 *
	 * @param {*} index
	 */
	StopRecordLocal(index) {
		const success = this.ocxContral.StopRecordLocal(this.ocxUI.GetHWnd(index));
		let path = this.ocxContral.recordLocalpath;
		delete this.ocxContral.recordLocalpath;
		if (success === 0) {
			message.success(
				<span>
					录像成功！
          <a
						onClick={() => {
							this.ocxContral.GotoDir(path);
						}}
					>
						打开文件所在目录
          </a>
				</span>
			);
		} else {
			message.info(<span>录像失败！</span>);
		}
	}

	/**
	 *
	 * @param {*} bAudioFlag       声音
	 * @param {*} bTalkFlag        话筒
	 * @param {*} bRecordFlag      录像
	 * @param {*} bQuickPlayFlag   即时回放
	 * @description                标签状态
	 */
	SetButtonStatus(bAudioFlag, bTalkFlag, bRecordFlag, bQuickPlayFlag) {
		this.ocxUI.SetButtonStatus(
			bAudioFlag,
			bTalkFlag,
			bRecordFlag,
			bQuickPlayFlag
		);
	}

	hideToolbarButton(buttonIndex, hide) {
		console.log('this.ocxUI====::', this.ocxUI)
		console.log('typeof this.ocxUI.hideToolbarButton====::', typeof this.ocxUI.hideToolbarButton)
		// if (this.ocxUI && (typeof this.ocxUI.hideToolbarButton === 'function')) {
		if (this.ocxUI) {
			this.ocxUI.hideToolbarButton(buttonIndex, hide);
		}
	}

	FullScreen() {
		this.ocxUI.SetFullScreen();
	}

	// 录像播放进度设置
	OssSeek(hwnd, position, callback) {
		// const Time = time - this.saveTime[index];
		// return this.ocxContral.OssSeek(this.ocxUI.GetHWnd(index), Time);
		const data = {
			hwnd,
			position,
		}
		this.DoWebICFunction(
			"OssSeek",
			JSON.stringify(data),
			function (res) {
				const result = JSON.parse(res);
				callback && callback(result);
			},
			1
		)
	}

	/**
	 *
	 * @param {*} szPUID
	 * @param {*} beginTime
	 * @param {*} endTime
	 * @param {*} type
	 * @param {*} reason
	 * @param {*} offset
	 * @param {*} count
	 * @param callback
	 * @description  获取录像文件
	 */
	QueryVodFile(
		szPUID,
		beginTime,
		endTime,
		type,
		reason,
		offset,
		count,
		callback
	) {
		const [_, puid, channelNo] = szPUID.split("-");
		const data = {
			src: 0,
			channel_code: channelNo,
			begin: beginTime - 1,
			end: endTime - 1,
			reason,
			type,
			offset,
			count,
		};
		this.DoWebICFunction(
			"QueryVodFile",
			JSON.stringify(data),
			function (res) {
				const result = JSON.parse(res);
				const errorCode = result.Error;
				if (errorCode === 0) {
					callback(result);
				} else {
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

	/**
	 *
	 * @param {*} i    窗口下标
	 * @description    录像暂停
	 */
	OSSVODPause(i) {
		const index = typeof i !== "undefined" ? i : this.ocxUI.GetCurWndIndex();
		this.ocxContral.OSSVODPause(this.ocxUI.GetHWnd(index));
	}

	/**
	 *
	 * @param {*} i    窗口下标
	 * @param callback
	 * @description    录像暂停
	 */
	OSSVODResume(callback, i) {
		const index = typeof i !== "undefined" ? i : this.ocxUI.GetCurWndIndex();
		// this.ocxContral.OSSVODResume(this.ocxUI.GetHWnd(index));
		const data = {
			hwnd: this.ocxUI.GetHWnd(index),
		}
		this.DoWebICFunction(
			"VodPlayResume",
			JSON.stringify(data),
			function (res) {
				const result = JSON.parse(res);
				const errorCode = result.Error;
				if (errorCode === 0) {
					callback && callback(result);
				} else {
					callback(false);
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

	/**
	 *
	 * @param {*} i        窗口下标
	 * @param {*} number   播放速度2的次方
	 * @description        播放速度
	 */
	OSSSetSpeed(number, i) {
		const index = typeof i !== "undefined" ? i : this.ocxUI.GetCurWndIndex();
		let puid = null;
		puid = this.getPUIDByIndex(index);
		if (puid) {
			this.ocxContral.OSSSetSpeed(this.ocxUI.GetHWnd(index), number);
		}
	}

	/**
	 *
	 * @param {*} i   下标
	 * @description   单针播放
	 */
	PlayOneByOne(i) {
		const index = typeof i !== "undefined" ? i : this.ocxUI.GetCurWndIndex();
		if (index >= 0) this.ocxContral.PlayByFrame(this.ocxUI.GetHWnd(index), 0);
	}

	/**
	 *
	 * @param {*} i   下标
	 * @description   单针停放
	 */
	ResumePlayByFrame(i) {
		const index = typeof i !== "undefined" ? i : this.ocxUI.GetCurWndIndex();
		if (index >= 0)
			this.ocxContral.ResumePlayByFrame(this.ocxUI.GetHWnd(index));
	}

	/**
	 *
	 * @param {*} i
	 * @param {*} x
	 * @param {*} y
	 * @param {*} order
	 * @description         电子放大
	 */
	ZoomInOut(x, y, order = 1, i) {
		const index = typeof i !== "undefined" ? i : this.ocxUI.GetCurWndIndex();
		if (index >= 0)
			this.ocxContral.ZoomInOut(this.ocxUI.GetHWnd(index), x, y, 0, 0, order);
	}

	/**
	 *
	 * @param {*} index
	 * @description  对讲
	 */
	OpenTalk(index) {
		let puid = null;
		for (const key in this.PdWndIndex) {
			if (this.PdWndIndex[key] === index) {
				puid = key;
			}
		}
		if (puid) {
			// const [spuid] = puid.split("-");
			// this.ocxContral.OpenTalk(spuid, 0, this.ocxUI.GetHWnd(index));
			const params = JSON.stringify({
				channel_code: 0,
				hwnd: this.ocxUI.GetHWnd(index),
			});
			this.DoWebICFunction("OpenTalk", params, res => { }, 1);
		}
	}

	/**
	 *
	 * @param {*} index
	 * @description      关闭对讲
	 */
	CloseTalk(index) {
		this.ocxContral.CloseTalk(this.ocxUI.GetHWnd(index));
	}

	/**
	 *
	 * @param {*} index
	 * @description  声音
	 */
	OpenSound(index) {
		this.ocxContral.OpenSound(this.ocxUI.GetHWnd(index));
	}

	/**
	 *
	 * @param {*} index
	 * @description     关闭声音
	 */
	CloseSound(index) {
		this.ocxContral.CloseSound(this.ocxUI.GetHWnd(index));
	}

	CloseBtn(index, callback) {
		const puid = this.getPUIDByIndex(index);
		if (puid) {
			if (puid.indexOf("/") >= 0) {
				this.delLoopStream(index, callback);
			} else {
				this._deleteVideo(puid, callback);
			}
		}
	}

	/** 
	 * 窗口是否锁定
	*/
	isLock(index) {
		return this.windowLocks[index] ? true : false
	}



	// ocx显示
	show() {
		this.ocxUI.show();
	}

	// ocx隐藏
	hide() {
		this.ocxUI.hide();
	}

	/**
	 *
	 * @param {*} index
	 * @param data
	 * @param startTime
	 * @param {*} endTime
	 *   绘制进度条信息
	 */
	setFileInfo(index, data, startTime, endTime) {
		// console.log(startTime, endTime, data.File[0].BeginTime)
		const startEdge = timestamp => {
			return timestamp < startTime ? startTime : timestamp;
		};
		const endEdge = timestamp => {
			return timestamp > endTime ? endTime : timestamp;
		};
		data.File.forEach(file => {
			this.progross.setFileInfo(
				index,
				startEdge(file.BeginTime),
				endEdge(file.EndTime),
				0,
				0
			);
		});
	}

	progressConfig(puid, name, date) {
		// 初始生成
		if (puid) {
			let index = 0;
			let keys = Object.keys(this.progrossInfo);
			// 自动按照顺序生成
			for (let i = 0; keys.length >= i; i++) {
				// 如果没有生成进度条替换掉
				if (
					typeof this.progrossInfo[i] === "undefined" ||
					!this.progrossInfo[i].progress
				) {
					index = i;
					break;
				}
			}
			if (typeof this.progrossInfo[index] !== "undefined") {
				this.progrossInfo[index] = { ...this.progrossInfo[index], name, puid };
			} else {
				this.progrossInfo[index] = {
					name,
					date,
					puid,
					vodProgress: 0,
					progress: false,
				};
			}
		} else if (date) {
			// 重新选择日期
			let keys = Object.keys(this.progrossInfo),
				len = keys.length,
				endInfo = this.progrossInfo[keys[len - 1]],
				del = false;
			if (endInfo) {
				// 有未绘制的列表
				if (!endInfo.progress) {
					for (let index in this.progrossInfo) {
						let info = this.progrossInfo[index];
						if (
							endInfo.puid === info.puid &&
							info.date === date &&
							index !== keys[len - 1]
						) {
							this.progrossInfo[keys[len - 1]].date = undefined;
							del = true;
							break;
						}
					}
					!del && (this.progrossInfo[keys[len - 1]].date = date);
				} else {
					let key = Number(keys[len - 1]) + 1;
					for (let index in this.progrossInfo) {
						let info = this.progrossInfo[index];
						if (endInfo.puid === info.puid && info.date === date) {
							//&& index !== keys[len-1]
							del = true;
							break;
						}
					}
					this.progrossInfo[key] = {
						...endInfo,
						date: del ? undefined : date,
						progress: false,
						vodProgress: 0,
					};
				}
			}
		}
	}

	/**
	 *
	 * @param {*} index
	 * @description 通过窗口索引获取对应列表
	 */
	getProgressListByIndex(index) {
		let progressList = null;
		let puid = this.getPUIDByIndex(index);
		if (puid !== null) {
			for (let index in this.progrossInfo) {
				let info = this.progrossInfo[index];
				if (info.puid + "-" + info.date === puid) {
					progressList = { ...info, index };
				}
			}
		}
		return progressList;
	}

	/**
	 * 设置/清空录像标签
	 * @param index
	 * @param name 名称,一般传入通道或者设备名称等. 名称传空串,则清空此进度信息
	 * @param data 时间日期
	 */
	setLabel(index, name, data) {
		this.progross.setLabel(index, name, data);
	}

	//播放进度查询
	getVODProgress(index) {
		return JSON.parse(
			this.ocxContral.GetVODProgress(this.ocxUI.GetHWnd(index))
		);
	}

	/**
	 *
	 * @param {*} puid
	 * @param {*} date
	 * @description 设置焦点
	 */
	setFocusIndex(puid, date) {
		for (let index in this.progrossInfo) {
			let info = this.progrossInfo[index];
			if (info.date === date && info.puid === puid) {
				this.progross.setFocusIndex(index);
				break;
			}
		}
	}

	/**
	 * 云台控制
	 * @param {*} type   枚举类型
	 * @param Step
	 * @description      方向控制
	 */
	PTZControl(type, Step) {
		const index = this.ocxUI.GetCurWndIndex();
		let puid = null;
		for (const key in this.PdWndIndex) {
			if (this.PdWndIndex[key] === index) {
				puid = key.split(":")[0];
				break;
			}
		}
		if (puid) {
			// this.ocxContral.PTZControl(
			//   type,
			//   puid.split("-")[1],
			//   puid.split("-")[2],
			//   Step
			// );
			const params = JSON.stringify({
				channel_code: puid.split("-")[2],
				ptz_type: type,
				degree: Step,
			})
			this.DoWebICFunction('PTZControl', params, res => { }, 0)
		}
	}

	// 添加预置点
	AddPresetPos = (puid, nPresetPos, pszName) => {
		// const returnValue = this.ocxContral.AddPresetPos(
		//   puid.split("-")[1],
		//   puid.split("-")[2],
		//   nPresetPos,
		//   pszName
		// );
		// if (returnValue === 0) {
		//   message.info("添加预置点成功！");
		// } else {
		//   message.error("添加预置点失败！");
		// }
		const params = JSON.stringify({
			channel_code: puid.split("-")[2],
			preset_pos: nPresetPos,
			preset_name: pszName,
		})
		this.DoWebICFunction('AddPresetPos', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("添加预置点成功！");
			} else {
				message.error("添加预置点失败！");
			}
		}, 0)
	};

	//删除预置点
	DelPresetPos = (puid, index) => {
		// const returnValue = this.ocxContral.DelPresetPos(
		//   puid.split("-")[1],
		//   puid.split("-")[2],
		//   index
		// );
		// if (returnValue === 0) {
		//   message.info("删除预置点成功！");
		// } else if (returnValue === 53) {
		//   message.info("该预置点正在被使用！");
		// } else {
		//   message.error(`删除预置失败!(${returnValue})`);
		// }
		const params = JSON.stringify({
			channel_code: puid.split("-")[2],
			preset_pos: index,
		})
		this.DoWebICFunction('DelPresetPos', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("删除预置点成功！");
			} else if (result.Error === 53) {
				message.info("该预置点正在被使用！");
			} else {
				message.error(`删除预置失败!(${result.Error})`);
			}
		}, 0)
	};

	//修改预置点
	ModifyPresetPos = (puid, index, name) => {
		// const returnValue = this.ocxContral.ModifyPresetPos(
		//   puid.split("-")[1],
		//   puid.split("-")[2],
		//   index,
		//   name
		// );
		// if (returnValue === 0) {
		//   message.info("预置点名称修改成功！");
		// } else {
		//   message.error("预置点名称修改失败！");
		// }
		const params = JSON.stringify({
			channel_code: puid.split("-")[2],
			preset_pos: index,
			preset_name: name,
		})
		this.DoWebICFunction('ModifyPresetPos', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("预置点名称修改成功！");
			} else {
				message.error("预置点名称修改失败！");
			}
		}, 0)
	};

	//查询预置点(异步)
	QueryPresetPos = (puid, src) => {
		// const id = this.ocxContral.QueryPresetPos(
		//   puid.split("-")[0],
		//   puid.split("-")[1],
		//   src
		// );
		// return new Promise((resolve, rej) => {
		//   this.asyncEvents.on(id, result => {
		//     if (result.Error === 0) {
		//       return resolve(result);
		//     } else {
		//       return rej(result);
		//     }
		//   });
		// });
		const params = JSON.stringify({
			channel_code: puid.split("-")[1],
			src,
		})
		return new Promise((resolve, reject) => {
			this.DoWebICFunction('QueryPresetPos', params, res => {
				const result = JSON.parse(res)
				if (result.Error === 0) {
					return resolve(result);
				} else {
					return reject(result);
				}
			}, 0)
		})
	};

	//移动到预置点
	GotoPreset = (puid, preset) => {
		// const returnValue = this.ocxContral.GotoPreset(
		//   puid.split("-")[1],
		//   Number(puid.split("-")[2]),
		//   Number(preset)
		// );
		// if (returnValue === 0) {
		//   message.info("移动成功！");
		// } else {
		//   message.error("移动失败！");
		// }
		const params = JSON.stringify({
			channel_code: Number(puid.split("-")[2]),
			preset_pos: Number(preset),
		})
		this.DoWebICFunction('GotoPreset', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("移动成功！");
			} else {
				message.error("移动失败！");
			}
		}, 0)
	};

	//添加巡航路径(设备)(异步)
	AddCruisePathDev = (puid, data) => {
		// const dataSource = JSON.stringify(data);
		// const returnValue = this.ocxContral.AddCruisePathDev(
		//   puid.split("-")[1],
		//   puid.split("-")[2],
		//   dataSource
		// );
		// if (returnValue === 0) {
		//   message.info("添加巡航路径成功！");
		// } else {
		//   message.error("添加巡航路径失败！");
		// }
		const params = JSON.stringify({
			channel_code: puid.split("-")[2],
			...data,
		})
		this.DoWebICFunction('AddCruisePathDev', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("添加巡航路径成功！");
			} else {
				message.error("添加巡航路径失败！");
			}
		}, 0)
	};

	// 巡航准备
	CruisePrepare(puid, cruiseInfo) {
		// const data = JSON.stringify(cruiseInfo);
		// this.ocxContral.CruisePrepare(puid.split("-")[1], data);
		const params = JSON.stringify({
			channel_code: puid.split('-')[2],
			curise: cruiseInfo.curise,
		})
		this.DoWebICFunction('PresetCruisePrepare', params, res => { }, 0)
	}

	// 开始巡航
	CruiseStart(puid) {
		// const returnValue = this.ocxContral.CruiseStart(
		//   puid.split("-")[1],
		//   puid.split("-")[2]
		// );
		// if (returnValue === 0) {
		//   message.info("播放成功！");
		// } else {
		//   message.error("播放失败！");
		// }
		const params = JSON.stringify({
			channel_code: puid.split('-')[2],
		})
		this.DoWebICFunction('PresetCruiseStart', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("播放成功！");
			} else {
				message.error("播放失败！");
			}
		}, 0)
	}

	//暂停巡航
	CruisePause(puid) {
		// this.ocxContral.CruisePause(puid.split("-")[1]);
		const params = JSON.stringify({
			channel_code: puid.split('-')[2],
		})
		this.DoWebICFunction('PresetCruisePause', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("暂停成功！");
			} else {
				message.error("暂停失败！");
			}
		}, 0)
	}

	//恢复巡航
	CruiseResume(puid) {
		// this.ocxContral.CruiseResume();
		const params = JSON.stringify({
			channel_code: puid.split('-')[2],
		})
		this.DoWebICFunction('PresetCruiseResume', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("恢复成功！");
			} else {
				message.error("恢复失败！");
			}
		}, 0)
	}

	//停止巡航
	CruiseStop(puid) {
		// const returnValue = this.ocxContral.CruiseStop(puid.split("-")[1]);
		// if (returnValue === 0) {
		//   message.info("停止成功！");
		// } else {
		//   message.error("停止失败！");
		// }
		const params = JSON.stringify({
			channel_code: puid.split('-')[2],
		})
		this.DoWebICFunction('PresetCruiseStop', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("停止成功！");
			} else {
				message.error("停止失败！");
			}
		}, 0)
	}

	//查询巡航状态
	CruiseStatus(puid) {
		// this.ocxContral.CruiseStatus(puid.split("-")[1]);
		const params = JSON.stringify({
			channel_code: puid.split('-')[2],
		})
		this.DoWebICFunction('PresetCruiseStatus', params, res => { }, 0)
	}

	// 添加巡航路径(数据库)(异步)
	AddCruisePathDB = (puid, data) => {
		// const dataSource = JSON.stringify(data);
		// const returnValue = this.ocxContral.AddCruisePathDB(
		//   puid.split("-")[1],
		//   puid.split("-")[2],
		//   dataSource
		// );
		// if (returnValue === 0) {
		//   message.info("添加巡航路径成功！");
		// } else {
		//   message.error("添加巡航路径失败！");
		// }
		const params = JSON.stringify({
			channel_code: puid.split("-")[2],
			...data,
		})
		this.DoWebICFunction('AddCruisePathDB', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("添加巡航路径成功！");
			} else {
				message.error("添加巡航路径失败！");
			}
		}, 0)
	};

	//查询巡航路径(数据库)(异步)
	QueryCruisePathDB = puid => {
		// const id = this.ocxContral.QueryCruisePathDB(
		//   puid.split("-")[1],
		//   puid.split("-")[2]
		// );
		// return new Promise((resolve, rej) => {
		//   this.asyncEvents.on(id, result => {
		//     if (result.Error === 0) {
		//       return resolve(result);
		//     } else {
		//       return rej(result);
		//     }
		//   });
		// });
		const params = JSON.stringify({
			channel_code: puid.split("-")[2],
		})
		return new Promise((resolve, reject) => {
			this.DoWebICFunction('QueryCruisePathDB', params, res => {
				const result = JSON.parse(res)
				if (result.Error === 0) {
					return resolve(result);
				} else {
					return reject(result);
				}
			}, 0)
		})
	};

	//删除巡航路径(数据库)
	DelCruisePathDB = (puid, pathCode) => {
		// const returnValue = this.ocxContral.DelCruisePathDB(
		//   puid.split("-")[1],
		//   puid.split("-")[2],
		//   pathCode
		// );
		// if (returnValue === 0) {
		//   message.info("删除巡航路径成功！");
		// } else {
		//   message.error("删除巡航路径失败！");
		// }
		const params = JSON.stringify({
			channel_code: puid.split("-")[2],
			pathCode,
		})
		this.DoWebICFunction('DelCruisePathDB', params, res => {
			const result = JSON.parse(res)
			if (result.Error === 0) {
				message.info("删除巡航路径成功！");
			} else {
				message.error("删除巡航路径失败！");
			}
		}, 0)
	};

	/**
	 * 开始下载
	 * 录像文件下载接口
	 * @param puid 第一段所属组织id,第二段puid,第三段通道号,第四段通道类型，第五段设备名称
	 * @param startTime
	 * @param endTime
	 * @param name
	 */
	OSSFileDLStart(puid, startTime, endTime, name) {
		// const returnValue = this.ocxContral.OSSFileDLStart(
		//   `${puid.split("-")[1]}$0$0$${puid.split("-")[2]}-${startTime}-${endTime}`,
		//   name
		// );
		// return message.info(`开始下载接口返回值${returnValue}`);
		const params = JSON.stringify({
			channel_code: `${puid.split("-")[1]}$0$0$${puid.split("-")[2]}`,
			src: 0,
			begin: startTime,
			end: endTime,
			name,
			speed: 0,
		});
		this.DoWebICFunction("DownloadByTime", params, res => {
			message.info(`开始下载接口返回值${res}`)
		}, 1);
	}

	//停止下载
	OSSFileDLStop(puid, startTime, endTime) {
		this.ocxContral.OSSFileDLStop(
			`${puid.split("-")[1]}$${puid.split("-")[3]}$0$${puid.split("-")[2]}`,
			startTime,
			endTime
		);
	}

	//暂停下载
	OSSFileDLPause(puid, channel, startTime, endTime) {
		const returnValue = this.ocxContral.OSSFileDLPause(
			`${puid}$0$0$${channel}-${startTime}-${endTime}`
		);
		return message.info(`暂停下载接口返回值${returnValue}`);
	}

	//继续下载
	OSSFileDLResume(puid, channel, startTime, endTime) {
		const returnValue = this.ocxContral.OSSFileDLResume(
			`${puid}$0$0$${channel}-${startTime}-${endTime}`
		);
		return message.info(`继续下载接口返回值${returnValue}`);
	}

	/**
	 * 删除下载文件
	 * @param puid
	 * @param channel
	 * @param startTime
	 * @param endTime
	 * @param deleteFlag 0: 只标记为删除. 1删除文件,但保留记录 2删除文件和记录
	 * @returns {MessageType}
	 * @constructor
	 */
	OSSFileDLDelete(puid, channel, startTime, endTime, deleteFlag) {
		let state = 2;
		if (deleteFlag !== 2) {
			state = !deleteFlag ? 1 : 2;
		}
		const data = `${puid}$0$0$${channel}-${startTime}-${endTime}`;
		const returnValue = this.ocxContral.OSSFileDLDelete(data, state);
		return message.info(`删除接口返回值${returnValue}`);
	}

	//查询全部下载状态   返回值: String
	OSSFileDLStatus() {
		this.ocxContral.OSSFileDLStatus();
	}

	bindProgress(index) {
		this.ocxContral.bindProgress(
			this.ocxUI.GetHWnd(index),
			this.progross.getId()
		);
	}

	SwitchStream(code) {
		const windowValue = this.ocxUI.GetCurWndIndex()
		const jsonParams = {
			hwnd: this.ocxUI.GetHWnd(windowValue),
			stream: code,
		}
		this.DoWebICFunction('SwitchStream', JSON.stringify(jsonParams), (res) => {
			// console.log(res)
			const data = JSON.parse(res)
			if (data.Error === 0) {
				message.info(`切换到${code === 0 ? '主' : '辅'}码流`)
			} else {
				message.info(`切换${code === 0 ? '主' : '辅'}码流失败`)
			}
		}, 0)
	}

	async checkOcx() {
		// 判断版本是否一样，不一样要求更新ocx版本
		console.log('local ocx version ------> ', this.ocxContral.version && this.ocxContral.version());
		const ocxConfig = await getOcxConfig()
		console.log('ocx configs -----> ', ocxConfig)
		return this.ocxContral.version && this.ocxContral.version() === ocxConfig.ocxVersion;
	}

	getDataPath() {
		return this.ocxContral.GetDataPath();
	}

	/**
	 * OCX通用接口调用封装
	 * @param funName OCX接口名称
	 * @param jsonParams OCX接口参数
	 * @param callback
	 * @param async 调用方式 0-同步 1-异步 不传默认异步
	 * @constructor
	 */
	DoWebICFunction(funName, jsonParams, callback, async) {
		this.ocxContral.DoWebICFunction(funName, jsonParams, callback, async);
	}
}
