/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 11:12:38
 * @lastTime: 2020-12-15 09:23:35
 * @FilePath: \biz-scene-attendance-web\src\components\CommonTransfer\index.jsx
 * @Description: 公共穿梭框
 */
import React from 'react';
import { Transfer } from '@hz-components/mg-biz';
import { 
    getDepartmentMembersTree,
    getDeviceGroupTree,
    getDeviceListTree,
    getExpressGroupTree,
    getUserOrgIndex,
    getOrganizationsTree,
} from '@services/common';
import { API_ERROR_CODE } from '@constants';
import styles from './index.less';
import { useRef } from 'react';
import { treeSetting, searchSetting, treeTarget } from './utils';
// import { useEffect, useState } from 'react';

// let fun = [];  // 存储考勤点资源树的promise

const CommonTransfer = (props) => {
    const { getTransferSelectedData, type, overlayType='page', getTreeParams, getTree, treeSet, searchSet } = props;
    const treeRef = useRef(null)

    const getTreeFn = {
        // 考勤组
        deviceGroup: () => {
            return new Promise((resolve, reject) => {
                getDeviceGroupTree().then(res => {
                    if(res && res.error_code === API_ERROR_CODE) {
                        resolve({
                            isSuccess: true,
                            msg: '',
                            data: res.data || [],
                        });
                    }
                }).catch(()=>{
                    resolve({
                        isSuccess: true,
                        msg: '',
                        data: [],
                    });
                })
            })
        },
        // 考勤设备
        device: () => {
            return new Promise((resolve) => {
                getDeviceListTree(getTreeParams).then(res => {
                    if(res) {
                        resolve({
                            isSuccess: true,
                            msg: '',
                            data: res.data || [],
                        });
                    }
                }).catch(()=>{
                    resolve({
                        isSuccess: true,
                        msg: '',
                        data: [],
                    });
                })
            })
        },
        person: () => {
            return new Promise((resolve, reject) => {
                getDepartmentMembersTree(getTreeParams).then(res => {
                    if(res) {
                        resolve({
                            isSuccess: true,
                            msg: '',
                            data: res.data || [],
                        });
                    }
                }).catch((res)=>{
                    resolve({
                        isSuccess: true,
                        msg: '',
                        data: [],
                    });
                })
            })
        },
        expression: () => {
            return new Promise((resolve, reject) => {
                getExpressGroupTree().then(res => {
                    if(res && res.error_code === API_ERROR_CODE) {
                        resolve({
                            isSuccess: true,
                            msg: '',
                            data: res.data || [],
                        });
                    }
                }).catch(()=>{
                    resolve({
                        isSuccess: true,
                        msg: '',
                        data: [],
                    });
                })
            })
        },
        organization: () => {
            return new Promise((resolve, reject) => {
                getUserOrgIndex().then(res => {
                    if(res) {
                        getOrganizationsTree({
                            orgIndex: res.org_index,
                            type: 1,
                        }).then(respose => {
                            if(respose) {
                                resolve({
                                    isSuccess: true,
                                    msg: '',
                                    data: respose || [],
                                });
                            }
                        })
                    }
                }).catch(()=>{
                    resolve({
                        isSuccess: true,
                        msg: '',
                        data: [],
                    });
                })
            })
        },
    }

    // 穿梭框选中数据
    const onChange = (data, total) => {
        // console.log(data, total)
        if(getTransferSelectedData) {
            getTransferSelectedData(data, total);
        }
    }

    // 给设备节点填加父级节点的code
    // const getTreeFromParent = (pCode = '', data = []) => {
    //     data.map(item => {
    //         if(item && item.child_num > 0) {
    //             getTreeFromParent(item.resource_code, item.child)
    //         }
    //         return item.parent_resource_code = pCode;
    //     })
    //     return data
    // }

    return (
        <div className={overlayType === 'page' && styles.TransferStyle}>
            <Transfer
                overlayType={overlayType}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                onChange={onChange}
                getTreeFn={getTree || getTreeFn[type]}
                treeSetting={treeSet || treeSetting[type]}
                mapSetting={{}}
                treeTarget={treeTarget}
                searchSetting={searchSet || searchSetting[type]}
                height={470}
                deleteTips={false}
                ref={treeRef}
                {...props}
            />
        </div>
    )
}

CommonTransfer.defaultProps = {
    type: 'device',  // deviceGroup--考勤组  device--考勤点   person--人员   expression--表达式  organization--组织（云南中职）
    getTransferSelectedData: () => {},  // 选择资源树数据
    getTreeParams: {}, // 资源树请求要传的参数
}

export default CommonTransfer;