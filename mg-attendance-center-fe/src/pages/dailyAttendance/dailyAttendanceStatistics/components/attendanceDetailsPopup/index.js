/**
 * 考勤明细 弹窗
 */
import React, { useEffect, useState } from 'react';
import {
	Button,
	Modal,
	Icon,
	Popover,
	message,
	Table,
	Spin,
} from 'antd';

import ReactScrollbar from 'react-custom-scrollbars';
import {
	modeEnum,
	getEnumValue,
	modeType,
	attendanceResEnum,
	REQUEST_SUCCESS,
	attendanceResType,
	leaveTypeEnum,
	sexEnum,
	filterNullParam,
} from '../../utils';
import {
	infoEnum,
	tableColumns,
} from './utils';
import { getPersonWeekDetails } from '../../../../../services/dailyAttendance';
import { getAttendanceDetailList } from '../../../../../services/checkAttendance/checkAttendanceDetail';
import personEmpty from '@hz-design/base/public/person-fail-empty.svg';
import styles from './index.less';

function AttendanceDetailsPopup(props) {
	const {
		visible,
		personNo,
		selectWeekDate,
		taskId,
		onCancel,
		title = '考勤详情',
	} = props;
	// 信息  数据
	const [info, setInfo] = useState(null);
	// loading
	const [loading, setLoading] = useState(false);

	/**
	 * 获取数据
	 */
	const getData = async () => {
		setLoading(true);
		// 存在任务id则是抽查考勤
		const api = taskId ? getAttendanceDetailList : getPersonWeekDetails;
		try {
			const result = await api(filterNullParam({
				task_id: taskId,
				person_no: personNo,
				attendance_date: selectWeekDate,
			}));
			const {
				data,
				error_code,
				message: msg,
			} = result || {};
			if (error_code === REQUEST_SUCCESS) {
				const {
					attendance_time,
					attendance_date,
					person_info,
					leave_info,
					practice_info,
					task_info = {},
				} = data;
				data.person_info = {
					...person_info,
					gender: getEnumValue(sexEnum, person_info.gender),
				};
				data.attendance_info = {
					...task_info,
					attendance_time,
					attendance_date,
				};
				practice_info && (data.practice_info = {
					...practice_info,
					time_frame: `${practice_info.start_date} ~ ${practice_info.end_date}`,
				});
				leave_info && (data.leave_info = {
					leave_type: getEnumValue(leaveTypeEnum, leave_info.type),
					time: `${leave_info.start_date} ~ ${leave_info.end_date}`,
					...leave_info,
				});
				setInfo({
					// 默认设置这两个字段为空数组
					recent_records: [],
					change_records: [],
					...data,
				});
			} else {
				message.error(msg);
			}
		} catch (err) {
			console.log(err);
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		getData();
	// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<Modal
			visible={visible}
			centered
      maskClosable={false}
			width={700}
      bodyStyle={{
          height: 580,
          position: 'relative',
      }}
			onCancel={onCancel}
			title={title}
			footer={
				<Button onClick={onCancel}>取消</Button>
			}
		>
      <ReactScrollbar renderTrackHorizontal={() => <div />}>
        {
          loading ? (
            <div className={styles['loading']}>
              <Spin />
            </div>
          ) : (
            <div
              className={styles['info-container']}
            >
              {
                info && (
                  <div
                    className={styles.tag}
                    style={{
                      color: getEnumValue(attendanceResEnum, info.result, 'color'),
                      backgroundColor: getEnumValue(attendanceResEnum, info.result, 'bgc'),
                    }}
                  >
                    {getEnumValue(attendanceResEnum, info.result)}
                  </div>
                )
              }
              {
                info ? Object.keys(infoEnum).map((key) => {
                  const {
                    title,
                    type,
                    list,
                  } = infoEnum[key];
                  return info[key] && (
                    <div
                      key={key}
                    >
                      {/* <h5 className={styles.title}>{title}</h5> */}
                      <div className='hz-label'>{title}</div>
                      <ul className={type === 'step' ? styles.row : styles.column}>
                        {
                          type === 'list' && Object.keys(list).map(i => {
                            return (
                              info[key][i] && (
                                <li
                                  key={i}
                                  className={styles.item}
                                >
                                  <span className={styles.label}>{list[i]}</span>
                                  {
                                    i === 'person_url'
                                      ? (
                                      <img
                                        src={info[key][i] || personEmpty}
                                        className={styles.avatar}
                                        alt='照片'
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = personEmpty;
                                        }}
                                        />
                                      )
                                      : <span className={styles.value}>{info[key][i]}</span>
                                  }
                                </li>
                              )
                            );
                          })
                        }
                        {
                          type === 'table' && (
                            <Table
                              rowKey='id'
                              columns={tableColumns}
                              dataSource={info[list]}
                              pagination={false}
                            />
                          )
                        }
                        {
                          type === 'step' && (
                            info[list].length > 0 ? info[list].map((item, index) => {
                              return (
                                <li
                                  className={styles['step-item']}
                                  key={item.changed_at + index}
                                >
                                  <div className={styles['step-info']}>
                                    <span
                                      className={styles.state}
                                      style={{
                                        color: getEnumValue(attendanceResEnum, item.result, 'color'),
                                        backgroundColor: getEnumValue(attendanceResEnum, item.result, 'bgc'),
                                      }}
                                    >
                                      {getEnumValue(attendanceResEnum, item.result)}
                                    </span>
                                        <span>
                                      {getEnumValue(modeEnum, item.mode)}
                                          {
                                            item.mode === modeType.MANUAL && (
                                              <Popover
                                                title='原因备注'
                                                content={item.comment}
                                                overlayClassName='attendance-details-popup-popover'
                                              >
                                                <Icon
                                                  type='hz-tips'
                                                  style={{
                                                    marginLeft: 5,
                                                  }}
                                                />
                                              </Popover>
                                            )
                                          }
                                    </span>
                                    <span>{item.changed_at}</span>
                                  </div>
                                  {
                                    info[list].length !== index + 1 && <Icon type='hz-next-arrow' />
                                  }
                                </li>
                              );
                            }) : <li className={styles['not-edit']}>无更改记录</li>
                          )
                        }
                      </ul>
                      {
                        (key === 'attendance_info' && info.result !== attendanceResType.NORMAL) && (
                          <div className={styles['not-attendance-record']}>无考勤记录</div>
                        )
                      }
                    </div>
                  );
                }) : <div className={styles['not-attendance']}>考勤记录不存在</div>
              }
            </div>
          )
        }
      </ReactScrollbar>
		</Modal>
	);
}

export default AttendanceDetailsPopup;
