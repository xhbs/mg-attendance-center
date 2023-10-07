/*
 * @Author: hesisi 00444
 * @Date: 2020-05-12 14:26:44
 * @Description: Description
 */

import React from 'react';
import { LocaleProvider } from 'antd';
import zhCN from 'antd/es/locale-provider/zh_CN';
import moment from 'moment';
import 'moment/locale/zh-cn';
import BasicLayout from './BasicLayout';

moment.locale('zh-cn');
const Index = () => (
  <LocaleProvider locale={zhCN}>
    <BasicLayout />
  </LocaleProvider>
);
export default Index;
