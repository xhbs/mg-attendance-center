/*
 * @Description:
 * @Author: zhangjie
 * @Date: 2020-07-15 15:17:23
 */
import { Table } from '@hz-components/react-base';

const { Ellipsis, EnumSelect, RangePicker } = Table;
const showTime = 'HH:mm:ss';

export function tableColumns(columns = []) {
	const data = columns.map(v => {
		return {
			title: () => <div style={{width: 128}}><Ellipsis>{v.name}</Ellipsis></div>,
			dataIndex: v.code,
			width: 150,
			render: text => <Ellipsis>{text}</Ellipsis>,
			className: 'ellipsis',
		};
	});
	return data;
}

export function tableSearchOptions(props) {
	const { taskList = [] } = props;
	return {
		trigger: null,
		conditions: [
			{
				label: '考勤任务',
				render: getFieldDecorator => {
					return getFieldDecorator('task_id', {
						initialValue: '',
					})(<EnumSelect hasAll list={taskList} />);
				},
			},
			{
				label: '考勤日期',
				render: getFieldDecorator => {
					return getFieldDecorator('date')(
						<RangePicker
							showTime={{ format: showTime }}
							placeholder={['开始日期', '结束日期']}
							dropdownClassName="dateDropdown"
							format={'YYYY-MM-DD'}
						/>,
					);
				},
			},
		],
	};
}
