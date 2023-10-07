/**
 * 模板选择 弹框
 */

import React from 'react';
import { Modal, Button } from 'antd';

function TemplateSelect(props) {
	const {
		visible,
		title,
		onCancel,
		onSelect,
	} = props;

	return (
		<Modal
			visible={visible}
			title={title}
			onCancel={onCancel}
			footer={null}
		>
			<div
				style={{
					textAlign: 'center',
				}}
			>
				<Button
					style={{
						marginRight: 10,
					}}
					onClick={() => onSelect(1)}
				>
					助学金资助名单
				</Button>
				<Button
					onClick={() => onSelect(0)}
				>
					免学费资助名单
				</Button>
			</div>
		</Modal>
	);
}

export default TemplateSelect;
