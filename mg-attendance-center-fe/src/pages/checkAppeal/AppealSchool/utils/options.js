/*
 * @Author: xiedan WX042
 * @since: 2020-12-03 17:00:43
 * @lastTime: 2021-08-26 17:55:51
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\checkAppeal\AppealSchool\utils\options.js
 * @Description: 
 */
import moment from 'moment';
import { Table } from '@hz-components/react-base';
import { APPEAL_STATUS, DATE_TYPE, DATE_TYPE_DAY } from '@constants';

const { Ellipsis, RangePicker, EnumSelect } = Table;


export function tableColumns(obj) {
	const { handleDetail, handleAppeal, handleDel } = obj;
	return [
        {
            title: '处理状态',
            dataIndex: 'status',
            width: '10%',
            render: text => {
                const target = APPEAL_STATUS.find(item => text === item.code) || {};
                return <div style={{color: target.color}}>{target.name || '--'}</div>
            },
            sorter: true,
            defaultSortOrder: 'ascend',
            sortDirections: ['descend', 'ascend'],
        },
        {
            title: '申诉标题',
            dataIndex: 'title',
            width: '20%',
            render: (text, record) => {
                return (
                    <Ellipsis>
                        <a onClick={() => handleDetail(record)}>
                        {text || '--'}
                        </a>
                    </Ellipsis>
                );
            },
        },
        {
            title: '申诉内容',
            dataIndex: 'content',
            width: '20%',
            render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '考勤时段',
            dataIndex: '$time',
            width: '15%',
            render: (text, record) => {
                let str = '--';
                let start = record.start_time && moment(record.start_time).format(DATE_TYPE_DAY) || '--';
                let end = record.end_time && moment(record.end_time).format(DATE_TYPE_DAY) || '--';
                if (record.start_time || record.end_time) {
                    str = `${start}~${end}`
                }
                return <Ellipsis>{str}</Ellipsis>
            },
        },
        {
            title: '申诉组织',
            dataIndex: 'org_path_name',
            width: '15%',
            render: text => <Ellipsis>{text || '--'}</Ellipsis>,
        },
        {
            title: '申诉时间',
            dataIndex: 'reported_at',
            width: '10%',
            render: text => <Ellipsis>{text && moment(text).format(DATE_TYPE) || '--'}</Ellipsis>,
        },
        {
            title: '操作',
            dataIndex: 'x',
            width: '10%',
            align: 'center',
            render: (t, record) => {
                const { status } = record;
                return (
                    <>
                        <a
                            onClick={() => { status === 1 && handleAppeal(record.id) }}
                            style={{
                                marginRight: 14,
                                color: status !== 1 && '#999',
                                cursor: status !== 1 && 'not-allowed',
                            }}
                        >
                            申诉
                        </a>
                        <a
                            onClick={() => { status !== 2 && handleDel(record.id) }}
                            style={{
                                marginRight: 14,
                                color: status === 2 && '#999',
                                cursor: status === 2 && 'not-allowed',
                            }}
                        >
                            删除
                        </a>
                    </>
                )
            },
        },
    ]
}

export function tableSearchOptions() {
	return {
		trigger: null,
		conditions: [
			{
				label: '处理状态',
				render: getFieldDecorator => {
					return getFieldDecorator('status', {
						initialValue: '',
					})(<EnumSelect hasAll list={APPEAL_STATUS} />);
				},
			},
			{
				label: '考勤时段',
				render: getFieldDecorator => {
					return getFieldDecorator('date')(
						<RangePicker
							dropdownClassName="dateDropdown"
							format={DATE_TYPE_DAY}
							showTime={{ format: "HH:mm:ss" }}
						/>,
					);
				},
			},
		],
	};
}
