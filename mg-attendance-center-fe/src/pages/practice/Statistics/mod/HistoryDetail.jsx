/*
 * @Author: xiedan WX042
 * @since: 2020-11-02 17:40:01
 * @lastTime: 2021-08-27 15:29:25
 * @文件相对于项目的路径: \biz-scene-attendancesys-fe\src\pages\practice\Statistics\mod\HistoryDetail.jsx
 * @Description: 历史考勤详情弹框
 */
import React, { useEffect, useState } from 'react';
import { Modal, Spin, Tooltip, Divider, DatePicker, message, Button, Table  } from 'antd';
import classNames from 'classnames';
import moment from 'moment';
import ReactScrollbar from 'react-custom-scrollbars';
import { getHistoryDetail } from '@/services/practiceStatistics';
import { API_ERROR_CODE, DATE_TYPE_DAY, STATISTICS_TYPE_MAP } from '@constants';
import Calendar from '@components/Calendar';
import GraphOrList from '@components/GraphOrList';
import { getMonthBetween } from '@utils/utils';
import emptyImg from "@hz-design/base/public/simple-empty.svg";
import { smallColumns } from '../utils/tableOptions';
import PieComponent from './Pie';
import styles from '../styles/historyDetail.less';

const { RangePicker } = DatePicker;

const HistoryDetail = (props) => {
    const { visible, onCancel, data } = props;
    const [modelType, setModelType] = useState('graph');  // 列表图形模式切换 graph  list
    const [date, setDate] = useState([ moment().subtract(7, 'days').startOf('day'), moment().endOf('day') ]); // 时间筛选，默认近7天
    const [statistics, setStatistics] = useState([]);  // 图形统计的数据
    const [total, setTotal] = useState(0); // 图形统计总数
    const [absenteeism, setAbsenteeism] = useState(0); // 缺勤率
    const [dataSource, setDataSource] = useState({}); // 日历的数据  {'2020-01': [{"date": "2020-12-25", "result": "实习", "type": 10}], '2020-02': []}
    const [dataTableSource, setDataTableSource] = useState([]); // 列表的数据
    const [monthsList, setMonthList] = useState(
        getMonthBetween(
            moment(moment().subtract(7, 'days').startOf('day')).format('YYYY-MM-DD'),
            moment(moment().endOf('day')).format('YYYY-MM-DD')
        )
    ); // 时间段内的所有月份集合 ['2020-01', '2020-02', '2020-03']
    const [currentPage, setCurrentPage] = useState(1); // 历史详情框表格页码，用于计算列表数据的序号
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        getDetail()
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [data, date])

    // 获取详情
    const getDetail = () => {
        setLoading(true);
        if(date && date.length === 2) {
            const params = {
                person_no: data.person_no,
            }
            params.start_time = moment(
                moment(date[0]).format('YYYY-MM-DD') + ' 00:00:00',
            ).valueOf();
            params.end_time = moment(moment(date[1]).format('YYYY-MM-DD') + ' 23:59:59')
                .set('millisecond', 999)
                .valueOf();

            getHistoryDetail(params).then(res => {
                if (res && res.error_code === API_ERROR_CODE) {
                    const { records = [], statistics = [] } = res.data || {};
                    // 构造日历数据, 按月分类
                    let obj = {};
                    const newRecords = [...records];
                    monthsList.forEach(i => {
                        const a = newRecords.filter(item => {
                            return i === item.date.substr(0,7);
                        });
                        obj[i] = a || [];
                    })
                    // 构造统计图形数据
                    let m = 0;
                    let n = 0;
                    const arr1 = statistics.map(item => {
                        m = m + item.count;
                        // 99--缺勤
                        if(item.type === 99 || item.type === '99') {
                            n = (item.percent || item.percent === 0) ? item.percent.toFixed(2) : '--';
                        }
                        return {
                            value: (item.count || item.count === 0) ? item.count : '--',
                            name: STATISTICS_TYPE_MAP[item.type] || '--',
                            percent: (item.percent || item.percent === 0) ? `${item.percent.toFixed(2)}%` : '--',
                        }
                    })

                    setStatistics(arr1 || []);
                    setTotal(m);
                    setAbsenteeism(n);
                    setDataSource(obj || {}); // {'2020-01': [{"date": "2020-12-25", "result": "实习", "type": 10}], '2020-02': []}
                    setDataTableSource(records || []);
                }
            }).finally(() => {
                setLoading(false);
            })
        }
    }
    
    // 日期范围
    const onChangeRangePicker = (date, dateString) => {
        const a = moment(dateString[1]).diff(moment(dateString[0]), 'days')
        if( a > 360 ) {
            return message.error('考勤日期范围不能大于360天');
        }
        const result = getMonthBetween(dateString[0], dateString[1]);
        setMonthList(result);
        setDate(date);
        // getDetail();
    }

    const disabledDate = (current) => {
        // Can not select days before today and today
        return current && current >= moment().endOf('day');
    }

    return (
        <Modal
            visible={visible}
            title="历史考勤"
            width={800}
            bodyStyle={{
                position: 'relative',
                height: 680,
            }}
            centered
            destroyOnClose
            maskClosable={false}
            onCancel={onCancel}
            footer={[
              <Button onClick={onCancel}>
                取消
              </Button>,
            ]}
            >
            <ReactScrollbar renderTrackHorizontal={() => <div />}>
                <Spin spinning={loading}>
                    <div className={styles.detailWapper}>
                        <div className='hz-label'>学生信息</div>
                        <div className={classNames({
                            'hz-layout-grid': true,
                            [styles.infoWapper]: true,
                            })}
                            style={{padding: '10px'}}
                        >
                            <ul
                                className={classNames({
                                    'hz-row': true,
                                })}
                            >
                                <li className='hz-col hz-col-3'>姓名</li>
                                <li className='hz-col hz-col-4'>
                                    <Tooltip title={data.person_name}>
                                        <div className='ellipsis'>
                                            {data.person_name || '--'}
                                        </div>
                                    </Tooltip>
                                </li>
                                <li className='hz-col hz-col-3'>学号</li>
                                <li className='hz-col hz-col-4'>
                                    <Tooltip title={data.person_no}>
                                        <div className='ellipsis'>
                                            {data.person_no || '--'}
                                        </div>
                                    </Tooltip>
                                </li>
                                <li className='hz-col hz-col-3'>所属组织</li>
                                <li className='hz-col hz-col-7'>
                                    <Tooltip title={data.org_path_name}>
                                        <div className='ellipsis'>
                                            {data.org_path_name || '--'}
                                        </div>
                                    </Tooltip>
                                </li>
                            </ul>
                        </div>
                        <Divider style={{margin: '10px 0 20px 0'}} />
                        {/* 图形区域 */}
                        <div>
                            <span style={{marginRight: '10px'}}>考勤日期</span>
                            <RangePicker
                                value={date}
                                format={DATE_TYPE_DAY}
                                disabledDate={disabledDate}
                                onChange={onChangeRangePicker}
                                allowClear={false}
                            />
                        </div>
                        <div className={styles.graphicsWapper}>
                            <div className={styles.left}>
                                <div className='hz-label'>统计概览</div>
                                <div className={styles.graphicLeft}>
                                    <PieComponent
                                        graphData={statistics}
                                        total={total}
                                        domId="pieLeft"
                                        picClassName={styles.pieWrap}
                                        type='number'
                                    />
                                </div>
                            </div>
                            <div className={styles.right}>
                                <div className='hz-label'>缺勤率</div>
                                <div className={styles.graphicRight}>
                                    <PieComponent
                                        graphData={[absenteeism]}
                                        domId="pieRight"
                                        picClassName={styles.pieWrap}
                                        type='percent'
                                    />
                                </div>
                            </div>
                        </div>
                        {/* 日历和表格 */}
                        <div>
                            <div className='hz-label'>
                                考勤分布
                                <GraphOrList
                                    handleTypeChange={(v) => {
                                        console.log(v)
                                        setModelType(v.target.value)
                                    }}
                                />
                            </div>
                            {
                                modelType === 'graph' ? (
                                    /*dataSource----{'2020-01': [{"date": "2020-12-25", "result": "实习", "type": 10}], '2020-02': []}*/
                                    dataTableSource.length < 1 ? (
                                        <div style={{textAlign: 'center', margin: '40px', color: '#ccc'}}>
                                            <img alt='' src={emptyImg} />
                                            <p>暂无数据</p>
                                        </div>
                                    ) : (
                                        <>
                                            <div style={{margin: '15px 0', height: 350, overflowY: 'auto'}}>
                                                {
                                                    Object.keys(dataSource).map(i => {
                                                        return <Calendar time={i} captionType='none' align='center' dataSource={dataSource[i]} />
                                                    })
                                                }
                                            </div>
                                            
                                            <div className={styles.datesList} style={{margin: '0 auto'}}>
                                                <div className={styles.colorItem}><span className={styles.blue} /> 正常</div>
                                                <div className={styles.colorItem}><span className={styles.red} /> 缺勤</div>
                                                <div className={styles.colorItem}><span className={styles.green} /> 请假</div>
                                                <div className={styles.colorItem}><span className={styles.yellow} /> 实习</div>
                                            </div>
                                        </>
                                    )
                                ) : (
                                    <div style={{margin: '15px 0', height: 250}}>
                                        <Table
                                            columns={smallColumns(currentPage)}
                                            dataSource={dataTableSource}
                                            pagination={{
                                                total: dataTableSource.length,
                                                pageSize: 10,
                                                size: 'small',
                                                showQuickJumper: true,
                                                showTotal: total => `共 ${total} 条数据`,
                                                onChange: (page) => {
                                                    console.log(page)
                                                    setCurrentPage(page);
                                                },
                                            }}
                                            size='small'
                                            scroll={{ y: 150 }}
                                        />
                                    </div>
                                )
                            }
                        </div>
                    </div>
                </Spin>
            </ReactScrollbar>
        </Modal>
    )
}

HistoryDetail.defaultProps ={
    visible: false,
    onCancel: () => {},
    data: [], // 外面的数据
}

export default HistoryDetail;