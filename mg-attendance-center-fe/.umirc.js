/*
 * @Aurhor: dengsha
 * @Date: 2020-05-11 17:32:43
 * @LastEditors: rdpnr_tang.pengcheng tpc1919561288@gmail.com
 * @Description:
 * @lastTime: 2021-10-08 14:13:42
 */
const path = require('path');
const Plugin = require.resolve('@hz/loader/plugins/umi-plugins');
const hzConfig = require('./env/index');
const rootdir = path.join(__dirname, '.');

// 根目录下的文件
function resolveCwd(...args) {
	args.unshift(process.cwd());
	return path.join(...args);
}

// ref: https://umijs.org/config/
const config = {
	treeShaking: true,
	extraBabelIncludes: [/@hz/],
	define: {
		'process.config': hzConfig,
	},
	context: {
		hzConfig: { ...hzConfig },
	},
	targets: {
		ie: 11,
	},
	publicPath: '/sceneAttendance/',
	outputPath: '/server/dist',
	routes: [
		{
			path: '/',
			component: '../layouts/index',
			exact: false,
		},
	],
	cssLoaderOptions: {
		modules: true,
		getLocalIdent: (context, localIdentName, localName) => {
			if (
				context.resourcePath.includes('node_modules') ||
				context.resourcePath.includes('ant.design.pro.less') ||
				context.resourcePath.includes('global.less')
			) {
				return localName;
			}
			const match = context.resourcePath.match(/src(.*)/);
			if (match && match[1]) {
				const antdProPath = match[1].replace('.less', '');
				const arr = antdProPath
					.split('/')
					.map((a) => a.replace(/([A-Z])/g, '-$1'))
					.map((a) => a.toLowerCase());
				return `antd-pro${arr.join('-')}-${localName}`.replace(/--/g, '-');
			}
			return localName;
		},
	},
	lessLoaderOptions: {
		javascriptEnabled: true,
		modifyVars: {
			hack: `true; @import "${resolveCwd('src/theme.less')}";`,
		},
	},
	extraBabelPlugins: [
		[
			'import',
			{
				libraryName: '@hz-components/ists-common',
				libraryDirectory: 'es',
				style: true,
			},
			'@hz-components/ists-common',
		],
		[
			'import',
			{
				libraryName: '@hz-components/biz-scene-components',
				libraryDirectory: 'es',
				style: true,
			},
			'@hz-components/biz-scene-components',
		],
		[
			'import',
			{
				libraryName: '@hz-components/mg-biz',
				libraryDirectory: 'es',
				style: true,
			},
			'@hz-components/mg-biz',
		],
	],
	alias: {
		// 自定义别名建议用“@”开头
		'@': path.resolve(rootdir, 'src/'),
		'@pages': path.resolve(rootdir, 'src/pages'),
		'@components': path.resolve(rootdir, 'src/components'),
		'@src': path.resolve(rootdir, 'src'),
		'@utils': path.resolve(rootdir, 'src/utils'),
		'@assets': path.resolve(rootdir, 'src/assets'),
		'@theme': path.resolve(rootdir, 'src/theme.less'),
		'@constants': path.resolve(rootdir, 'src/constants'),
		'@services': path.resolve(rootdir, 'src/services'),
	},
	externals: { react: 'React', 'react-dom': 'ReactDOM', vplay: 'vplay' },
	plugins: [
		// ref: https://umijs.org/plugin/umi-plugin-react.html
		[
			'umi-plugin-react',
			{
				antd: true,
				dva: {
					hmr: true,
				},
				dynamicImport: false,
				title: 'hz-template-web-fe',
				dll: false,

				routes: {
					exclude: [/components\//],
				},
			},
		],
		[
			'umi-plugin-untd',
			{
				importDirectory: 'es',
				libraryName: '@hz-components/react-base',
			},
		],
		Plugin,
	],
	proxy: {
		'/sso/st': {
			// target: hzConfig.infraApiOrigin,
			target: 'https://sso.ynjy.cn/51fefb9b0673440281764f1224b5bbd2/',
			changeOrigin: true,
		},
		'/api/infra-uuv/': {
			// target: `http://10.210.33.72:40087/`,
			// target: hzConfig.infraApiOrigin,
			target: 'https://sso.ynjy.cn/51fefb9b0673440281764f1224b5bbd2/',
			changeOrigin: true,
		},
		'/api/infra-uuv/v0.1/users/privilege-menus-tree': {
			// target: hzConfig.infraApiOrigin,
			target: 'https://sso.ynjy.cn/51fefb9b0673440281764f1224b5bbd2/',
			changeOrigin: true,
		},
		// 公共服务字典---厂商
		'/api/infra-udm/': {
			target: 'http://10.210.33.56:40087',
		},
		'/api/map/': {
			target: 'http://10.210.33.56:16550/',
		},
		'/api/ugis': {
			target: 'http://10.210.33.56:16550',
		},
		'/api/dynamic-form': {
			// 动态表单
			target: `http://10.210.33.56:14202`,
		},
		// '/api/bss/v1/dictionary/dics': {
		// 	// 字典服务
		// 	target: `http://10.210.32.63:11134`,
		// },
		// '/api/biz-scene/v1/file/upload': {
		// 	// 文件服务
		// 	target: `http://10.210.32.63:14204`,
		// },
		// vms chrome视频播放插件
		'/bclient/commonresource/': {
			target: `http://10.210.33.56:9090`,
		},
		'/api/vms/**': {
			target: `http://10.210.33.56:8060`,
		},

		'/api/uss/v1/ats/': {
			target: `http://10.210.33.56:16505/`,
		},
		// '/api/uss/v1/base/': {
		// 	target: `http://10.210.32.63:16504/`,
		// },
		// 文件注册
		'/api/mg/v1/fms/files': {
			target: `http://10.210.33.58:14177`,
		},
		// 文件上传
		'/api/mg/v1/fms/files/upload': {
			target: `http://10.210.33.58:14178`,
		},
		// 本地调试需要
		'/api/mg/v1/object/manage/': {
			target: `http://10.210.33.56:14548/`,
		},
		'/api/biz-scene/v1/attendance/': {
			// target: `http://192.168.1.74:14208/`,
			// target: `http://10.20.74.14:14208/`,
			target: 'https://sso.ynjy.cn/51fefb9b0673440281764f1224b5bbd2/',
			changeOrigin: true,
		},
		//抽查考勤明细导出接口代理
		'/api/biz-scene/v1/attendance/spot-check-attend/export-list': {
			// target: `http://10.210.32.64:14208/`,
			target: `http://10.210.23.80:14208/`,
		},
		//抽查考勤弹窗信息接口代理
		'/api/biz-scene/v1/attendance/spot-check-attend/details': {
			target: `http://10.210.6.114:14208/`,
		},
	},
};

export default config;
