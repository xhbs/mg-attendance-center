/*
 * @Author: heqiang 00621
 * @Date: 2020-05-11 14:22:42
 * @Description: Description
 */
import "@hz-design/base";

if (process && process.env && process.env.NODE_ENV === 'development') {
  window.hzConfig = process.config || {};
}
window.UgisConfig = {
  ugisRouteService: process.config.common.ugis.route || "",
  ugisStylePath: process.config.common.ugis.style || "",
  ugisServerIp: process.config.common.ugis.serverIp || "",
};
