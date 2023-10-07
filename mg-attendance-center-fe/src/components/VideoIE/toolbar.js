/*
 * @Author: xiedan WX042
 * @since: 2020-10-09 17:57:30
 * @lastTime: 2020-10-12 18:40:39
 * @FilePath: \biz-scene-coree:\hz\biz-scene-attendance-web\src\components\VideoIE\toolbar.js
 * @Description: 底部工具栏
 */
import React, { Component } from "react";
import { Row, Col, Popover } from "antd";
import styles from "./index.less";

export default class ToolBar extends Component {
	splitScreen = [1, 4, 9, 16];
	state = {
		visible: false, // 下载模态框
	};

	render() {
		const {
			isCloseAll,
			isSplitScreen,
			isFullScreen,
			splitScreen,
			changeScreen,
		} = this.props;

		return (
			<div
				className={styles.toolbar}
				style={{ width: `100%` }}
			>
				<Row>
					<Col span={17}>
						{
							isCloseAll && (
								<i
									type="poweroff"
									title="关闭全部"
									style={{ float: 'right' }}
									className={styles.closeAll}
									onClick={() => {
										this.props.closeAllVideo()
									}}
								/>
							)
						}
					</Col>
					<Col span={7}>
						{
							isSplitScreen && (
								<Popover
									content={
										<div className={styles.splitScreenList}>
											{this.splitScreen.map(item => {
												return (
													<span
														className={
															item === splitScreen
																? styles.active
																: ""
														}
														key={item}
														onClick={changeScreen(item)}
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
						{
							isFullScreen && (
								<i
									className={styles.fullScreen}
									title="全屏"
									onClick={() => {
										window.video.api.FullScreen();
									}}
								/>
							)
						}
					</Col>
				</Row>
			</div>
		);
	}
}
