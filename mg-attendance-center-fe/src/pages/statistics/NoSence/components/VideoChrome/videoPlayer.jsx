/*
 * @Author: hesisi 00444
 * @Date: 2020-06-02 10:03:39
 * @Description: Description
 */

import React, { PureComponent } from "react";
import { isEqual } from "lodash";
import { VideoPlayer } from "@hz-components/react-base";

class Player extends PureComponent {
  constructor(props) {
    super(props);

    this.videoPlayerRef = React.createRef();
  }

  shouldComponentUpdate(nextProps) {
    const { data, camera_id, url } = this.props;
    if (
      !isEqual(nextProps.data, data) ||
      !isEqual(nextProps.camera_id, camera_id) ||
      !isEqual(nextProps.url, url)
    ) {
      return true;
    }

    return false;
  }

  getRef = videoPlayerRef => {
    if (videoPlayerRef.current) {
      const player = videoPlayerRef.current;
      player.on("error", e => {
        const { data } = this.props;
        const message =
          (e && typeof e === "string" && e.replace(/\d\w+/, "")) || "";
        player.showMessage(
          `${
            data && data.device_name && message
              ? `【${data.device_name}】: ${message}`
              : e
          } `
        );
      });
    }
  };

  // TODO 规避脏数据造成的插件异常
  getCustom = () => {
    const params = this.props;
    const { custom } = params;
    if (!custom || !Array.isArray(custom)) {
      return custom;
    }

    return custom
      .map(v => v.channel_code || v.channel_id || v.rtmp_url)
      .filter(x => x);
  };

  render() {
    const params = this.props;

    return (
      <VideoPlayer getRef={this.getRef} {...params} custom={this.getCustom()} />
    );
  }
}

export default Player;
