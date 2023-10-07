const serverIp = 'http://192.168.109.160';

module.exports = {
	appUrl: `${serverIp}:8001`, // 当前node应用地址
	infraApiOrigin: `${serverIp}:80`, // （后台服务反向代理）(单点登陆所在服务器的地址)
	common: {
		ugis: {
			serverIp: 'http://192.168.121.30:16550',
			serverApi: 'http://192.168.121.30:16550',
		},
	},
};
