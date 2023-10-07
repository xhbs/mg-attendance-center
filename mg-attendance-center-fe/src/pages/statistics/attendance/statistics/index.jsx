import React, {  useRef, useState, useEffect } from 'react';
import { Table, Input, Button, Icon } from 'antd';
import { cloneDeep } from 'lodash';
import { getStudentAttSts } from '@/services/common';
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
			title: '月份',
			dataIndex: 'sts_month',
			ellipsis: true,
			key: 'sts_month',
			with: 96,
		},
		{
			title: '州市',
			key: 'zs',
			dataIndex: 'zs',
			ellipsis: true,
			with: 96,
			// sorter: (a, b) => a.zs.localeCompare(b.zs, 'zh-Hans-CN', { sensitivity: 'accent' }),
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
			// sorter: (a, b) => a.nj - b.nj,
			with: 46,
		},
		{
			title: '学生人数',
			dataIndex: 'xsrs',
			key: 'xsrs',
			with: 46,
			// sorter: (a, b) => a.xsrs - b.xsrs,
		},
		{
			title: '抓拍次数',
			dataIndex: 'snap_sum',
			key: 'snap_sum:',
			with: 46,
			// sorter: (a, b) => a.snap_sum - b.snap_sum,
		},
		{
			title: '点名次数',
			dataIndex: 'check_sums',
			key: 'check_sums:',
			with: 46,
			// sorter: (a, b) => a.check_sums - b.check_sums,
			// render: (text, record) => record.yxjrs - record.zcrs,
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
		total: 0,
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
		let { success, value } = await getStudentAttSts({
			pageNumber: pagina.current,
			pageSize: pagina.pageSize,
			...searchObject,
		}).finally(() => {
			setLoading(false);
		});
		console.log(value, 'value--')
    	if (success && value?.pageData) {
			console.log(value?.pageData, 'value?.pageData, value---');
			let list = value?.pageData ? cloneDeep(value?.pageData) : [];
			setList(list);
			setPagina({
				...pagina,
				total: value?.pageInfo?.totalCount || 0,
			});
		} else {
			setList([]);
			setPagina({
				...pagina,
				total: 0,
			});
		}
	};
	useEffect(() => {
		getList();
	// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [pagina.current, pagina.pageSize, JSON.stringify(searchObject)]);
	return (
		<div>
			<Table
				size="small"
				columns={columns}
				dataSource={[...list]}
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
