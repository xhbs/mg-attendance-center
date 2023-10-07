/**
 * 申诉统计
 */
import React, { useState, useEffect } from 'react';
import classNames from 'classnames';
import { getUserRoleInfo, getUserOrgIndex } from '@/services/common';
import { SCHOOL } from '@constants';
import LeftTreeSelect from '@components/LeftTreeSelectCom';
import RightTable from './mod/RightTable';
import styles from './styles/index.less';

function DailyAttendanceStatistics() {
  const [userRole, setUserRole] = useState('');  // 用户角色
  const [activeTreeNode, setActiveTreeNode] = useState('');
  const [selectedKey, setSelectedKey] = useState(''); // 分开存储tree的值，不影响table
  const [orgIndx, setOrgIndx] = useState(''); // 当前用户所属的组织

  useEffect(() => {
    getUserRoleInfo().then(res => {
      if (res) {
        setUserRole(res[0] && res[0].role_code || '');
        // setUserRole('provincial_admin')
      }
    })
    UserOrgIndex()
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const UserOrgIndex = async () => {
    const { org_index } = await getUserOrgIndex();
    setOrgIndx(org_index);
  }

  
  const changeTreeNode = (node) => {
    setActiveTreeNode(node.org_index);
    setSelectedKey(node.org_index)
  }

  const selectedNodeFun = (node) => {
    setActiveTreeNode('');
    setSelectedKey(node.org_index)
  }

	return (
    <div className="hz-layout-vertical">
      <div className={classNames(
        'hz-layout-horizontal',
        styles.appealSWapper
      )}>
        <div className="hz-layout-fl" style={{
          width: 280,
          height: '100%',
          backgroundColor: '#fff',
        }}>
            {
              userRole ? (
                <LeftTreeSelect setActiveInfo={changeTreeNode} showSubType={userRole !== SCHOOL ? 4 : ''} selectedKey={selectedKey} />
              ) : null
            }
        </div>
        <div className="hz-layout-main" style={{ height: '100%' }}>
          <div className={classNames('hz-layout-vertical', styles.practiceBox)}>
            <div className="hz-layout-vertical-body">
              <RightTable
                activeTreeNode={activeTreeNode}
                selectedNode={selectedNodeFun}
                userRole={userRole}
                orgIndx={orgIndx}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
	);
}

export default DailyAttendanceStatistics;
