/**
 * 随机字符串生成器
 * @param length 字符串长度，默认 = 7
 * @returns {string}
 * @constructor
 */
export const CreateRandomstring = (length = 7) => {
    let string = "";

    let len = Math.max(7, length);
    while (len) {
        string += String.fromCharCode(
            Math.floor(Math.random() * 26) + "a".charCodeAt(0)
        );
        len--;
    }
    return string;
};

/**
 * 事件监听处理器
 * @type {{queue: {}, on(*=, *): void, emit(*, *=): void, removeListener(*): void}}
 * queue 事件容器：key = 事件ID，value = 事件回调
 * on 添加事件监听：id,callback
 * emit 触发事件回调：id, options/传入回到函数的参数
 * removeListener 移除事件监听：id
 */
export const asyncEvents = {
    queue: {},
    on(id, callback) {
        if (typeof id === "number" || typeof id === "string") {
            this.queue[id] = callback;
        }
    },
    emit(id, options) {
        if (typeof this.queue[id] === "function") {
            this.queue[id](options);
        }
    },
    removeListener(id) {
        if (this.queue[id]) {
            delete this.queue[id];
        }
    },
};

/**
 * IE 检测工具返回 true/false
 * @param userAgent
 * @returns {*}
 * @constructor
 */
export const IETester = (userAgent)=> {
    const UA = userAgent || navigator.userAgent;
    if (/msie/i.test(UA)) {
        return UA.match(/msie (\d+\.\d+)/i)[1];
    } else if (~UA.toLowerCase().indexOf("trident") && ~UA.indexOf("rv")) {
        return UA.match(/rv:(\d+\.\d+)/)[1];
    }
    return false;
}
