/* eslint-disable */
const signale = require('signale');
const envConfig = require('./index');

const baseConfig = {
	port: '8001', // 端口，默认 8001
	session: {
		name: 'USESSION',
		secret: 'Hello I am a long long long secret',
	},
	viewsPath: 'dist', // ejs模板文件目录，默认为 'views'
	// 是否插入集成框架文件，默认为 false
	// 用于生产环境时，平台服务开启插入，子服务关闭插入，否则会
	// 造成重复插入
	insertScripts: true,
	fetchConfig: () => {
		return new Promise((resolve, reject) => {
			try {
				const configs = {
					...envConfig,
				};
				resolve(configs);
			} catch (e) {
				signale.fatal('配置中心拉取数据异常');
				reject(e);
			}
		});
	},

	// 页面代理路由语句后、接口代理路由语句之前的自定义处理钩子
	beforeProxy: (app) => {},

	// 自定义错误处理，默认 render('error') 和对应错误状态码
	userErr: (app) => {
		app.use((err, req, res, next) => {
			// set locals, only providing error in development
			res.locals.message = err.message;
			res.locals.error = err;
			console.log('web服务器内部错误，请查看app.js---------------------------------', err);
			console.log('======================node res:', res);
			// render the error page
			res.status(err.status || 500);
			res.send('<p>something blew up</p>');
		});
	},
};

const withOutCasConfig = {
	afterProxy: (app, config) => {
		console.log('>>>>>>>>>>>>>>>>>>>>>>>', config);
		app.get('/*', (_, res) => {
			// 获取写入session和cookies信息
			res.render('index', { config });
		});
	},
};

module.exports = { ...baseConfig, ...withOutCasConfig };
