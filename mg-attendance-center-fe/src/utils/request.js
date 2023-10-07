/*
 * @Desc: 请求封装
 * @Author: ranguangyu
 * @Date: 2020-08-26 17:51:46
 */

import { message } from 'antd';
import { fetch } from '@hz-components/biz-scene-utils';

function getCookie(name) {
	let arr;
	const reg = new RegExp(`(^| )${name}=([^;]*)(;|$)`);
	if ((arr = document.cookie.match(reg))) {  // eslint-disable-line
	  return unescape(decodeURIComponent(arr[2]));
	}; 
	return '';
}

function getHeaders(headers = {}) {
  const userCode = getCookie('usercode') || 'admin';
  const userName = getCookie('username') || 'admin';
  // const userCode = 'province123';
  // const userCode = 'city123';
  // const userCode = 'school123';
  // const userCode = '18088479875';
  // const userName = '刘志樊';

  return {
    'Content-Type': 'application/json',
    Authorization: encodeURIComponent(`username:${userName}&usercode:${userCode}`),
    User: encodeURIComponent(`username:${userName}&usercode:${userCode}`),
    'Cache-Control': 'no-cache',
    Pragma: 'no-cache',
    ...headers,
  };
}

function request(opts) {
  return new Promise((resolve, reject) => {
    fetch({
      headers: getHeaders(),
      ...opts,
    }).then(result => {
      if (!result) {
        resolve(null);
        return;
      }
      // 说明：对象管理接口来自原子组件，没有error_code
      if (result && (result.error_code === '0000000000' || !result.error_code)) {
        resolve(Object.assign(result, {success: true}));
      } else {
        message.error(result.message);
        resolve.success = false
        reject(result);
      }
    }).catch((err)=>reject(err))
  });
}

export default request;
