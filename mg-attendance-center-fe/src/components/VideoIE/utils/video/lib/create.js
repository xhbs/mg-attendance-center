/*
 * @Author: quezhongyou
 * @Date: 2018-09-14 17:51:21
 * @Last Modified by: quezhongyou
 * @Last Modified time: 2018-10-08 19:11:49
 */
import { IETester } from "./util";

export default {
    _create({ container, style, classid, name }) {
        const ieVersion = IETester(),isAfterClassid = ieVersion && Number(ieVersion)<= 10 ;
        const object = document.createElement("object");
        object.setAttribute("id", name);
        object.setAttribute("name", name);
        object.setAttribute("style", style);
        !isAfterClassid && object.setAttribute("classid", classid);
        container.appendChild(object);
        isAfterClassid && object.setAttribute("classid", classid);
        return object;
    },

    _create2({ container, style, name }) {
        const iframe = document.createElement("iframe");
        iframe.setAttribute("id", name);
        iframe.setAttribute("style", style);
        container.appendChild(iframe);

        return iframe;
    },


    /**
     * @description 创建视频窗口
     * @param {容器，样式，classid} param0
     */
    createWebPlayWnd({ container, style, classid }) {
        return this._create({ container, style, classid, name: "WebPlayWnd" });
    },

    /**
     * @description 创建控制窗口
     * @param {容器，样式，classid} param0
     */
    createWebIC({ container, style, classid }) {
        return this._create({ container, style, classid, name: "WebIC" });
    },

    /**
     * @description 创建进度条
     * @param {容器，样式，classid} param0
     */
    createProgross({ container, style, classid }){
        return this._create({ container, style, classid, name: "Progross" });
    },
 /**
     * @description 创建地图
     * @param {容器，样式} param0
     */
    createMap({ container, style }){
      return  this._create2({container, style, name:"iframe"})
    },
};
