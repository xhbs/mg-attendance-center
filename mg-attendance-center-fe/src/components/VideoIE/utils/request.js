/*
 * @Desc: 请求封装
 * @Author: ranguangyu
 * @Date: 2020-08-26 17:51:46
 */

import { message } from 'antd';
import { fetch } from '@hz-components/biz-scene-utils';
import Cookies from 'js-cookie';

function getHeaders(headers = {}) {
  const userCode = Cookies.get('usercode');
  const userName = Cookies.get('username');
  return {
    'Content-Type': 'application/json',
    Authorization: escape(escape(`username:${userName}&usercode:${userCode}`)),
    User: escape(escape(`username:${userName}&usercode:${userCode}`)),
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
      if (result && !result.error_code) {
        resolve(result);
      } else {
        message.error(result.message);
        reject(result);
      }
    });
  });
}

export default request;
