/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 08:31:19
 * @lastTime: 2021-09-11 17:55:38
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\env\index.js
 * @Description:
 */

// const serverIp = 'http://10.210.32.65';
// const serverIp = 'http://10.210.32.64';
// const serverIp = 'http://10.210.33.42';
const serverIp = 'https://sso.ynjy.cn/51fefb9b0673440281764f1224b5bbd2';
const ugisIp = "10.210.32.63"; // ugis新地址

// vPlay配置
// const streamType = 0;
// const isLocal = 0;
// const streamMode = 1;
// const progress = true;

module.exports = {
	appUrl: `${serverIp}:8001`, // 当前node应用地址
	infraApiOrigin: `${serverIp}:40087`, // （后台服务反向代理）(单点登陆所在服务器的地址)
	common: {
		ugis: {
			serverIp: `http://${ugisIp}:16550`,
			serverApi: `http://${ugisIp}:16550`,
		},
	},

	// vPlayConfig: {
	// 	streamType, // 0:主码流 1:辅码流
	// 	isLocal, // 0:内网 1:外网
	// 	progress,
	// 	streamMode,
	// },

};
