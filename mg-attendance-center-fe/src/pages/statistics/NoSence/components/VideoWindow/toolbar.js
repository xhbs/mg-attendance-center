/*
 * @Author: xiedan WX042
 * @since: 2020-09-02 16:58:47
 * @lastTime: 2020-09-16 16:34:33
 * @FilePath: \biz-scene-coree:\hz\biz-scene-attendance-web\src\pages\statistics\NoSence\components\VideoWindow\toolbar.js
 * @Description: 
 */
import React, { Component } from "react";
import { Row, Col, Popover } from "antd";
import styles from "./index.less";

export default class ToolBar extends Component {
  splitScreenList = [1, 4, 9, 16];
  state = {
    visible: false, // 下载模态框
  };

  render() {
    const {
      splitScreen,
      changeScreen,
      disableSplit,
      closeAllVideo,
      handleCapture,
    } = this.props;
    return (
      <div
        style={{ width: `100%` }}
        className={styles.toolbar}
      >
        <Row>
          <Col span={10} offset={7}>
            <i title="批量抓拍" className={styles.shoot} onClick={handleCapture} />
            <i
              type="poweroff"
              title="关闭全部"
              className={styles.closeAll}
              onClick={() => closeAllVideo() }
            />
          </Col>
          <Col span={7}>
            {
              !disableSplit && (
                <Popover
                  content={
                    <div className={styles.splitScreenList}>
                      {this.splitScreenList.map(item => {
                        return (
                          <span
                            className={
                              item === splitScreen
                                ? styles.active
                                : ""
                            }
                            key={item}
                            onClick={() => changeScreen(item)}
                          >
                            {item}
                          </span>
                        );
                      })}
                    </div>
                  }
                  trigger="hover"
                  placement="left"
                >
                  <i title="分屏" className={styles.splitScreen} />
                </Popover>
              )
            }
            {/* <i
              className={styles.fullScreen}
              title="全屏"
            /> */}
          </Col>
        </Row>
      </div>
    );
  }
}
