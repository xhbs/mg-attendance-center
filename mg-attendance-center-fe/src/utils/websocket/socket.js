/*
 * @Author: wujingfei 00718
 * @Description: Description
 * @Date: 2019-04-19 11:06:42
 */

/**
 * @description: websocket封装
 * @param {object} options {
 *      @param {string} wsUrl websocket连接地址
 *      @param {object} socketHeader stomp连接请求头
 *      @param {function} onConnectSuccess stomp连接成功时调用
 *      @param {function} onConnectError stomp连接发生错误时调用
 *      @param {string} topic stomp订阅主题
 *      @param {function} onMessage 后台推送消息到前台时调用，已经将数据解析为json格式
 *      @param {function} once 第一次建立连接时调用，只调用一次（断线重连不调用）
 * }
 * @return: socket实例化对象，提供sendMessage及close两个方法与后台交互
 */
import SockJS from 'sockjs-client';
import { Stomp } from 'stompjs/lib/stomp';
import Cookies from "js-cookie";

const isDev = process.env.NODE_ENV === 'development'

export default class HzSocket {
    constructor(options) {
        this.options = options;
        this.connected = false;
        this.onInit();
    }

    onInit = () => {
        // const { once } = this.options;
        this.checkDependencies();
        this.initSocket();
    };

    /** 判断是否是外网 */
	socketHeader = () => {
        const { socketHeader } = this.options;
        if(socketHeader) return socketHeader

		if (isDev) return {
			userId: 1,
		}
		const outer = Cookies.get('outer')
		if (outer) {
			return {
				userId: 1,
				outer,
			}
		} else {
			return {
				userId: 1,
			}
		}
	}

    initSocket = () => {
        this.connected = false;
        // const { wsUrl, socketHeader } = this.options;
        const { wsUrl } = this.options;
        const sockClient = new SockJS(wsUrl);
        const stompClient = Stomp.over(sockClient);
        this.sockClient = sockClient;
        this.stompClient = stompClient;
        stompClient.connect(
            this.socketHeader(),
            () => {
                // if (once) once();
                this.connected = true
                this.handleConnectSuccess()
            },
            this.handleConnectError
        );
    };

    checkDependencies = () => {
        if (!SockJS || !Stomp) {
            throw new Error("HzSocket require sock.js and stomp.js!");
        }
    };

    handleConnectSuccess = () => {
        const { onConnectSuccess } = this.options;
        if (onConnectSuccess) onConnectSuccess();
        this.initSocketSubscribe();
    };

    handleConnectError = (e) => {
        const { onConnectError } = this.options;
        this.reconnectOnclose();
        if (onConnectError) onConnectError(e);
    };

    reconnectOnclose = () => {

        if (this.connected) {
            this.initSocket();
        }
    };

    initSocketSubscribe = () => {
        const { stompClient } = this;
        const { topic, onMessage } = this.options;
        if (stompClient) {
            stompClient.subscribe(topic, response => {
                let res;
                try {
                    res = JSON.parse(response && response.body);
                    // console.log('订阅的消息', res)
                } catch (error) {
                    res = response && response.body;
                }
                if (onMessage) onMessage(res);
            });
        }
    };

    /**
     * 调用方法服务器发送数据，参数与stomp.send相同
     */
    sendMessage = (messageUrl, header, message) => {
        const { stompClient: tompClient } = this;
        if (tompClient && tompClient.send) {
            tompClient.send(messageUrl, header, message);
        }
    };

    /**
     * 关闭socket连接方法
     */
    close = () => {
        const { sockClient } = this;
        this.connected = false;
        if (sockClient && sockClient.close) {
            sockClient.close();
        }
    };
}
