/*
 * @Desc: 请求封装
 * @Author: ranguangyu
 * @Date: 2020-08-26 17:51:46
 */

import { message } from 'antd';
import fetch from 'dva/fetch';

function getCookie(name) {
	let arr;
	const reg = new RegExp(`(^| )${name}=([^;]*)(;|$)`);
	if ((arr = document.cookie.match(reg))) {  // eslint-disable-line
	  return unescape(decodeURIComponent(arr[2]));
	}; 
	return '';
}

// 获取Header
function getNewOptions(option) {
  const newOptions = {
    ...option,
    credentials: "include",
  };

  newOptions.headers = {
    Accept: "*/*",
    "Cache-Control": "no-cache",
    Pragma: "no-cache",
    ...newOptions.headers,
    User: encodeURIComponent(`usercode:${getCookie("usercode")}&username:${getCookie("username")}`),
  };

  if (
    newOptions.method === "POST" ||
    newOptions.method === "PUT" ||
    newOptions.method === "DELETE"
  ) {
    if (!(newOptions.body instanceof FormData)) {
      newOptions.headers = {
        "Content-Type": "application/json; charset=utf-8",
        ...newOptions.headers,
      };
      newOptions.body = JSON.stringify(newOptions.body);
    } else {
      newOptions.headers = {
        ...newOptions.headers,
      };
    }
  }
  return newOptions;
}

function request(opts) {
  const newUrl = opts.url;
  const option = {...opts}
  delete option.url;
  const newOptions = getNewOptions(option);

  return new Promise((resolve, reject) => {
    fetch(newUrl, newOptions).then(result => {
        console.log(result)
        if (!result) {
          resolve(null);
          return;
        }
        // 说明：对象管理接口来自原子组件，没有error_code
        if (result && (result.error_code === '0000000000' || !result.error_code)) {
          resolve(result);
        } else {
          message.error(result.message);
          reject(result);
        }
      });
  });
}

export default request;
