/*
 * @Author: xiedan WX042
 * @since: 2020-12-03 17:00:43
 * @lastTime: 2021-10-08 14:08:33
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\checkAppeal\Appeal\utils\options.js
 * @Description: 
 */
import moment from 'moment';
import { Table } from '@hz-components/react-base';
import { LEAVE_DEAL_STATUS, DATE_TYPE, DATE_TYPE_DAY, SCHOOL_SUB_TYPE } from '@constants';

const { Ellipsis, RangePicker, EnumSelect } = Table;

export function tableColumns(obj) {
	const { userRole, handleDetail, handleDeal } = obj;
	return [
        {
            title: '处理状态',
            dataIndex: 'status',
            width: '10%',
            render: text => {
                const target = LEAVE_DEAL_STATUS.find(item => text === item.code) || {};
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
                // 已经处理的不需要再处理，只能审核下一级提交的申请
                // const flag = record.status !== 2 || (userRole === PROVINCE && !record.is_province_school);
                const flag = record.status !== 2 || (SCHOOL_SUB_TYPE[record.school_parent_sub_type] !== userRole);
                return (
                    <a
                        style={{
                            marginRight: 14,
                            color: flag && '#999',
                            cursor: flag && 'not-allowed',
                        }}
                        onClick={()=>{!flag && handleDeal(record)}}
                    >
                        处置
                    </a>
                )
            },
        },
    ]
}

export function tableSearchOptions(props) {
    // const { userRole } = props;
	return {
		trigger: null,
		conditions: [
			{
				label: '处理状态',
				render: getFieldDecorator => {
                    // 只有省级的才有 '5: 无需处理' 状态
                    // let list = [...LEAVE_DEAL_STATUS];
                    // if(userRole !== PROVINCE) {
                    //     list = list.filter(i => i.code !== 5);
                    // }
					return getFieldDecorator('status', {
						initialValue: '',
					})(<EnumSelect hasAll list={LEAVE_DEAL_STATUS} />);
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
