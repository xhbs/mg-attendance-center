/*
 * @Author: xiedan WX042
 * @since: 2020-12-03 17:00:43
 * @lastTime: 2021-01-25 16:24:14
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\statistics\Recommendation\utils\options.js
 * @Description: 
 */
import { Table } from '@hz-components/react-base';
import CommonTransfer from '@components/CommonTransfer';
import { Button, DatePicker } from 'antd';

const { Ellipsis } = Table;
const { MonthPicker } = DatePicker;
// const format = 'YYYY-MM-DD';


export function tableColumns(obj) {
	const { handleDetail, handleAddGroup } = obj;
	return [
        {
            title: '姓名',
            dataIndex: 'person_name',
            width: '15%',
            render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '学号',
            dataIndex: 'person_no',
            width: '15%',
            render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '所属组织',
            dataIndex: 'organization',
            width: '15%',
            render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '统计规则',
            dataIndex: 'describe',
            width: '15%',
            render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '统计时段',
            dataIndex: 'interval',
            width: '15%',
            render: (text) => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '缺勤天数',
            dataIndex: 'frequency',
            width: '10%',
            render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '操作',
            dataIndex: 'x',
            width: '15%',
            render: (t, record, index) => {
                return (
                    <>
                        <a onClick={() => { handleDetail(record, index) }} style={{marginRight: 16}}>缺勤详情</a>
                        <a onClick={() => { handleAddGroup(record) }}>加入考勤组</a>
                    </>
                )
            },
        },
    ]
}

export function tableSearchOptions(props) {
	const { value: {time, defaultKeysArr} } = props;
	return {
		trigger: null,
		conditions: [
			{
				label: '所属组织',
				render: getFieldDecorator => {
					return getFieldDecorator('group')(
                        <CommonTransfer
                            type='organization'
                            defaultKeys={defaultKeysArr}
                            // defaultKeys={defaultPersonTree}
							treeTarget={{}}
							overlayType='popover'
							getPopupContainer={null}
							customButtonValue={(value) => {
                                return (
                                    <Button style={{width: 220}}>
                                        {
                                            defaultKeysArr.length > 0 ? `已选${value || defaultKeysArr.length}个组织` : '全选'
                                        }
                                    </Button>
                                )
							}}
							checkStrictly
                        />,
					);
				},
			},
			{
				label: '统计时段',
				render: getFieldDecorator => {
					return getFieldDecorator('date', {
						initialValue: time,
					})(
                        <MonthPicker defaultValue={time} />,
						// <RangePicker
						// 	dropdownClassName="dateDropdown"
						// 	format={format}
						// 	showTime={{ format: "HH:mm:ss" }}
						// />,
					);
				},
			},
		],
	};
}
