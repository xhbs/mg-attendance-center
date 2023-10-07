/*
 * @Desc: 工具类
 * @Author: ranguangyu
 * @Date: 2020-09-17 16:14:10
 */

import enums from './enums';

/**
 * 根据已选code列表获取当前code列表
 */
export function getCodes(form) {
	const { common_express_list = [], notes_express_list = [] } = form.getFieldsValue();
	const compose_list = common_express_list
		.map(item => item.t_code)
		.concat(notes_express_list.map(item => item.j_code));
	const code = enums.code.filter(c => {
		const rest = compose_list.filter(item => item === c);
		if (rest.length > 0) {
			return false;
		}
		return true;
	});

	return code;
}

/**
 * 规则逻辑关系
 * @param {*} express
 * @param {*} code
 */
export function validateRules(express, code) {
	try {
		const x = express.match(/^[A-Z\|\|&&\(\)]+$/);
		if (!x) {
			return {
				error: true,
				message: '语法错误，使用了不支持的字符',
			};
		}

		const a = express.replace(/(\|\|)|(&&)/g, '');
		const b = a.match(/(\|)|(&)/g);
		if (b) {
			return {
				error: true,
				message: '语法错误，操作符使用有误',
			};
		}

		const c = express.match(/\(|\)/g);
		const d = [];
		if (c) {
			c.forEach(i => {
				if (i === '(') {
					d.push(i);
				} else {
					// 空栈时反括号不允许推入
					if (d.length === 0) {
						return {
							error: true,
							message: '语法错误，括号不匹配',
						};
					} else {
						// 与正括号进行消除
						d.length = d.length - 1;
					}
				}
			});
		}

		let e = express.match(/[A-Z]/g);
		e = [...new Set(e || [])];
		if (e.sort().join('') !== code.sort().join('')) {
			return {
				error: true,
				message: '规则与表达式编码不匹配',
			};
		}

		return {
			error: false,
		};
	} catch (e) {
		console.error(e);
		return {
			error: true,
			message: '语法错误',
		};
	}
}

/**
 * 格式化时间
 * @return string
 */
export function fixTime(time) {
	if (typeof time === 'string') {
		return time;
	}
	return time && time.format('HH:mm:ss');
}
