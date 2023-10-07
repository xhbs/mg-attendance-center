import React, { useEffect, useState } from 'react';
import { Modal } from 'antd';

function useModal(props) {
	const { children, title = '考勤明细' } = props;
	const [maxHeight, setMaxHeight] = useState(450);
	useEffect(() => {
		setMaxHeight(window.innerHeight - 270);
	}, []);

	return (
		<Modal
			destroyOnClose
			width={1000}
			centered={true}
			keyboard={false}
			maskClosable={false}
			bodyStyle={{ height: maxHeight }}
			title={title}
			{...props}
		>
			{children}
		</Modal>
	);
}

export const confirm = props =>
	Modal.confirm({
		...props,
		title: '确定删除所选数据吗？',
		content: '此操作不可逆，请慎重考虑。',
		okText: '删除',
		okType: 'danger',
		cancelText: '取消',
	});
export default useModal;
