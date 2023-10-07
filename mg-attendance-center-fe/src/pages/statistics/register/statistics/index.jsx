import React, {  useRef, useState, useEffect } from 'react';
import { Table, Input, Button, Icon } from 'antd';

import { getStudentRigiterStatis } from '@/services/common';
const Statistics = () => {
	const getColumnSearchProps = dataIndex => ({
		filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
			<div style={{ padding: 8 }}>
				<Input
					ref={node => {
						searchInputRef.current = node;
					}}
					placeholder={`搜索`}
					value={selectedKeys[0]}
					onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
					onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex)}
					style={{ width: 188, marginBottom: 8, display: 'block' }}
				/>
				<Button
					type="primary"
					onClick={() => handleSearch(selectedKeys, confirm, dataIndex)}
					icon="search"
					size="small"
					style={{ width: 90, marginRight: 8 }}
				>
					搜索
				</Button>
				<Button
					onClick={() => handleReset(clearFilters)}
					size="small"
					style={{ width: 90 }}
				>
					重置
				</Button>
			</div>
		),
		filterIcon: filtered => (
			<Icon type="search" style={{ color: filtered ? '#1890ff' : undefined }} />
		),
		onFilter: (value, record) =>
			record[dataIndex]
				.toString()
				.toLowerCase()
				.includes(value.toLowerCase()),
		onFilterDropdownVisibleChange: visible => {
			if (visible) {
				setTimeout(() => searchInputRef.current.select());
			}
		},
	});

	const columns = [
		{
			title: '州市',
			key: 'zs',
			dataIndex: 'zs',
			ellipsis: true,
			with: 96,
      // sorter: (a, b) => a.zs.localeCompare(b.zs, 'zh-Hans-CN', {sensitivity: 'accent'}),
			...getColumnSearchProps('zs'),
		},
		{
			title: '区县',
			dataIndex: 'qx',
			ellipsis: true,
			key: 'qx',
			with: 96,
			...getColumnSearchProps('qx'),
		},
		{
			title: '学校名称',
			dataIndex: 'xxjgmc',
			ellipsis: true,
			key: 'xxjgmc',
			with: 126,
			...getColumnSearchProps('xxjgmc'),
		},
		{
			title: '专业名称',
			dataIndex: 'zymc',
			ellipsis: true,
			key: 'zymc',
			...getColumnSearchProps('zymc'),
		},
		{
			title: '班级名称',
			dataIndex: 'bjmc',
			ellipsis: true,
			...getColumnSearchProps('bjmc'),
			key: 'bjmc',
			with: 46,
		},
		{
			title: '年级',
			dataIndex: 'nj',
			...getColumnSearchProps('nj'),
			key: 'nj',
			with: 46,
		},
		{
			title: '在籍人数',
			dataIndex: 'yxjrs',
			key: 'yxjrs',
			with: 46,
		},
		{
			title: '注册人数',
			dataIndex: 'zcrs',
			key: 'zcrs:',
			with: 46,
		},
		{
			title: '未注册',
			dataIndex: 'nozcrs:',
			key: 'nozcrs:',
			with: 46,
			render: (text, record) => record.yxjrs - record.zcrs,
		},
		
	];
	const searchInputRef = useRef()
	const [searchObject, setSearchObject] = useState({})

	
  const handleSearch = (selectedKeys, confirm, dataIndex) => {
		console.log(selectedKeys, 'selectedKeys--9899')
    confirm();
		setSearchObject({
			...searchObject,
			[dataIndex]: selectedKeys[0],
		})
    setPagina({
      ...pagina,
      current: 1,
    })
  };
	const handleReset = clearFilters => {
    clearFilters();
    setSearchObject({})
  };

  const [loading, setLoading] = useState(false);
	const [list, setList] = useState([]);
	const [pagina, setPagina] = useState({
		total: 1479659,
		current: 1,
		pageSize: 16,
	});
	const onChange = (pagination, filters, sorter, extra) => {
		console.log('params', pagination);
    setPagina({
      ...pagina,
      current: pagination.current,
      pageSize: pagination.pageSize,
    })

	};
	const getList = async () => {
    setLoading(true)
		let {success, value} = await getStudentRigiterStatis({
			pageNumber: pagina.current,
			pageSize: pagina.pageSize,
			...searchObject,
		}).finally(() => {
      setLoading(false)
    })
		console.log(value, 'value--')
    if (success && value) {
      setList(value.pageData || [])
      setPagina({
        ...pagina,
        total: value.pageInfo?.totalCount || 0,
      })
    } 
	};
	useEffect(() => {
		getList();
	// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [pagina.current, pagina.pageSize, JSON.stringify(searchObject)]);
	return (
		<div>
			<Table
				rowKey='id'
				size="small"
				columns={columns}
				dataSource={list}
        loading={loading}
				pagination={{
					total: pagina.total,
					current: pagina.current,
					pageSize: pagina.pageSize,
          showSizeChanger: true, 
          showQuickJumper: true,
          showTotal: (total) =>  `总共 ${total} 条`,
				}}
				onChange={onChange}
			/>
		</div>
	);
};

export default Statistics;
