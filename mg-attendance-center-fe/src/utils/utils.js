/*
 * @Author: xiedan WX042
 * @since: 2020-08-31 14:26:53
 * @lastTime: 2021-09-17 17:57:52
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\utils\utils.js
 * @Description: 
 */
import moment from 'moment';
import { message } from 'antd';

// 设置不可选日期  只可选未来时间（可选当天）
export const disabledDate = (current) => {
    return current && current < moment().startOf('day');
}

// 设置不可选日期  只可选过去时间（可选当天）
export const disabledDateBefore = (current) => {
    return current && current > moment().endOf('day');
}

// 设置不可选日期  可选过去指定时间段内一直到当前时间，比如range=10，表示只能选择过去10天到今天的日期范围
export const disabledDateBeforeRange = (current, range = 1) => {
    const aa = moment().subtract(range, 'days').startOf('day');
    return current && (current < aa || current > moment().endOf('day'));
}


// 设置不可选 时和分的范围
export const range = (start, end) => {
    const result = [];
    for (let i = start; i < end; i++) {
        result.push(i);
    }
    return result;
}

// 设置不可选时间
export const disabledDateTime = () => {
    return {
        disabledHours: () => range(0, 24).splice(4, 20),
        disabledMinutes: () => range(30, 60),
        disabledSeconds: () => [55, 56],
    };
}

export function throttle(fn, delay) {
    let startTime = new Date();

    return (...rest) => {
        let curTime = new Date()

        // 如果达到了规定的触发时间间隔，触发 handler
        if (curTime - startTime >= delay) {
            fn(...rest)
            startTime = curTime;
        }
    };
}


/**
 * @description: base64格式图片下载
 * @param {string} {fileName}  图片名
 * @param {string} {content} base64图片
 */
export function downloadImgFile(fileName, content) {
    let aLink = document.createElement('a');
    let blob = base64ToBlob(content); //new Blob([content]);

    let evt = document.createEvent("HTMLEvents");
    evt.initEvent("click", true, true);//initEvent 不加后两个参数在FF下会报错  事件类型，是否冒泡，是否阻止浏览器的默认行为
    aLink.download = fileName;
    aLink.href = URL.createObjectURL(blob);

    // aLink.dispatchEvent(evt);
    //aLink.click()
    aLink.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));//兼容火狐
}

/**
 * @description: base64转blob
 * @param {string} {code}  base64图片
 * @return {type} 
 */
export function base64ToBlob(code) {
    let parts = code.split(';base64,');
    let contentType = parts[0].split(':')[1];
    let raw = window.atob(parts[1]);
    let rawLength = raw.length;

    let uInt8Array = new Uint8Array(rawLength);

    for (let i = 0; i < rawLength; ++i) {
      uInt8Array[i] = raw.charCodeAt(i);
    }
    return new Blob([uInt8Array], {type: contentType});
}

/** 判断是否为IE内核 */
export const getExplorer = () => {
const explorer = window.navigator.userAgent
const compare = function (s) { return (explorer.indexOf(s) >= 0) }
const ie11 = function () { return ('ActiveXObject' in window) }
if (compare('MSIE') || ie11()) {
    return 'ie'
} else if (compare('Firefox') && !ie11()) {
    return 'Firefox'
} else if (compare('Chrome') && !ie11()) {
    if (explorer.indexOf('Edge') > -1) {
    return 'Edge'
    } else {
    return 'Chrome'
    }
} else if (compare('Opera') && !ie11()) {
    return 'Opera'
} else if (compare('Safari') && !ie11()) { return 'Safari' }
return 'ie'
}

/**
 * @description: 下载文件
 * @param {*}
 * @return {*}
 */
export const downloadFileStream = (
    fileName,
    fileStream,
    time = 1
  ) => {
    let timer = setTimeout(() => {
      message.info("开始下载，请耐心等待", 3);
      if (!!window.ActiveXObject || "ActiveXObject" in window) {
        if (window.navigator.msSaveBlob) {
          try {
            const blobObject = new Blob([fileStream]);
            window.navigator.msSaveBlob(blobObject, fileName);
            message.destroy();
          } catch (e) {
            message.error(e);
          }
        }
      } else {
        const blob = new Blob([fileStream]);
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
        window.URL.revokeObjectURL(link.href);
        message.destroy();
      }
      clearTimeout(timer);
      timer = null;
    }, time * 1000);
}

/**
 * @description: 获取指定时间段内的所有月份
 * @param string start YYYY-MM / YYYY-MM-DD
 * @param string end YYYY-MM / YYYY-MM-DD
 * @param string format 返回的每个日期的格式
 * @return {*}
 */
export const getMonthBetween = (start, end, format = 'YYYY-MM') => {
    const result = [];
    const s = start.split("-");
    const e = end.split("-");
    const min = new Date();
    const max = new Date();
    min.setFullYear(s[0], s[1] * 1 - 1, 1);//开始日期
    max.setFullYear(e[0], e[1] * 1 - 1, 1);//结束日期
    const curr = min;
    while (curr <= max) {
      const month = curr.getMonth();
      result.push(moment(curr).format(format));
      curr.setMonth(month + 1);
    }
    return result;
}

/**
 * 学年计算  9.1 ~ 次年7.31
 * @param oldNum 往前多少年
 * @returns {*[]}
 */
export const getSchoolYear = (oldNum = 5) => {
	const nowDate = new Date();
	const nowYear = nowDate.getFullYear();
	const nowMonth = nowDate.getMonth() + 1;
	const endYear = nowMonth > 7 ? nowYear + 1 : nowYear;
	const yearOption = [];
	for (let i = 0; i < oldNum; i++) {
		const end = endYear - i;
		const start = end - 1;
		yearOption.push(`${start}-${end}`);
	}
	return yearOption;
};

/**
 * @description: 获取学期（春季 or 秋季）
 * @param {type} {type}
 * @return: 
 */
// export const getSemester = (schoolYear) => {
//   if(!schoolYear) return [];
//   const [start, end] = schoolYear.split('-');
// 	const nowDate = new Date();
// 	const nowYear = nowDate.getFullYear();
//   const nowMonth = nowDate.getMonth() + 1;

//   let arr = [
//     { code: '1', name: '秋季' },
//     { code: '0', name: '春季' },
//   ];

  // 去掉未来时间
  // if(nowYear === Number(start) && nowMonth >= 9) {
  //   arr = [
  //     { code: '1', name: '秋季' },
  //   ];
  // } else if (nowYear === Number(end) && nowMonth < 3) {
  //   arr = [
  //     { code: '1', name: '秋季' },
  //   ];
  // }

//   return arr
// }

/**
 * 获取学期 月份
 * @param schoolYear 学年
 * @param semester 学期
 * @returns {*}
 */
export const getSchoolMonth = (schoolYear, semester) => {
  if(!schoolYear) return [];
	// const nowDate = new Date();
	// const nowYear = nowDate.getFullYear();
  // const nowMonth = nowDate.getMonth() + 1;
  const [start, end] = schoolYear.split('-');
  
	const springMonth = ['03', '04', '05', '06', '07'];
	const autumnMonth = ['09', '10', '11', '12', '01'];
	let springMonthList = springMonth.map(item => `${end}-${item}`);
	let autumnMonthList = autumnMonth.map(item => {
		if (item > 7) {
			return `${start}-${item}`;
		}
		return `${end}-${item}`;
  });
  // 去掉未来时间
  // if(nowYear >= start && nowYear <= end) {
  //   springMonthList = springMonthList.filter(i => moment(i).isSameOrBefore(`${nowYear}-${nowMonth}`));
  //   autumnMonthList = autumnMonthList.filter(i => moment(i).isSameOrBefore(`${nowYear}-${nowMonth}`));
  // }

	const list = [springMonthList, autumnMonthList];
	return  list[semester] || [...autumnMonthList, ...springMonthList];
}
