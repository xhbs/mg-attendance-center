/*
 * @Author: xiedan WX042
 * @since: 2020-10-12 17:51:21
 * @lastTime: 2020-10-12 19:01:08
 * @FilePath: \biz-scene-coree:\hz\biz-scene-attendance-web\src\components\VideoIE\utils\index.js
 * @Description: 
 */
import Cookies from 'js-cookie';
import request from './request';

const isDev = process.env.NODE_ENV === 'development'

// 获取ocx登录IP地址
export const getOcxIp = async () => {
    const ocxConfig = await getOcxConfig()
    if (isDev) {
      return ocxConfig.videoLogin.ip
    }
  
    const ipRegx = /\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/;
    const applicationUrl = decodeURIComponent(Cookies.get('application_url'))
    const regxResult = applicationUrl.match(ipRegx)
    return regxResult ? regxResult[0] : ocxConfig.videoLogin.ip
};

export function getOcxConfig() {
    // const ocxConfigUrl = isDev ? '/config.json' : '/bclient/commonresource/config.json'
    const ocxConfigUrl = `/bclient/commonresource/config.json`
    return request({
      url: ocxConfigUrl,
      method: 'GET',
    })
}

export const toLoginPage = () => {
    const content = this.window.parent;
    // window.parent.postMessage(
    content.postMessage(
      JSON.stringify({
        title: "视频监控",
        type: 1,
      }),
      window.location.origin
      // "*"
    );
    window.location.href = `/`;
};

// 获取ocx登录用户名
export const getUsername = () => {
    if (isDev) {
      return "admin"
    }
    const username = Cookies.get('usercode')
    if (username) {
      return username;
    } else {
      toLoginPage();
    }
};

// 获取ocx登录token
export async function getToken(params) {
  // const { hostname } = window.location;
  // const baseURL = !isDev ? `http://${hostname}:40087` : 'http://192.168.109.161:40087';
  const url = `/sso/st?${Object.entries(params).map(([k, v]) => `${k}=${v}`).join('&')}`
  console.log('url======::', url)
  const res = await request({
    url,
    method: 'GET',
  })
  console.log('res=======', res)
  if (res && res.data) {
    window.sessionStorage.setItem('token', res.data.st)
    return res.data.st
  }
  return  ''
}
