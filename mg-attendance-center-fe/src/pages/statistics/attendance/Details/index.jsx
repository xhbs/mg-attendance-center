import React, { useState, useEffect, useRef } from 'react';
import { Table, Icon, Input, Button } from 'antd';
import { cloneDeep } from 'lodash';
import { getStudentAttDetailSts } from '@/services/common';
const Details = () => {
  const searchInputRef = useRef()
  const [searchObject, setSearchObject] = useState({})

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
  const handleSearch = (selectedKeys, confirm, dataIndex) => {
    confirm();
    console.log(selectedKeys, dataIndex, 'selectedKeys')
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
			...getColumnSearchProps('qx'),
			with: 96,
		},
		{
			title: '学校名称',
			dataIndex: 'xxjgmc',
			ellipsis: true,
			key: 'xxjgmc',
			...getColumnSearchProps('xxjgmc'),
			with: 126,
		},
		{
			title: '专业名称',
			dataIndex: 'zymc',
			ellipsis: true,
			...getColumnSearchProps('zymc'),
			key: 'zymc',
		},
		{
			title: '班级名称',
			dataIndex: 'bjmc',
			ellipsis: true,
			key: 'bjmc',
			with: 46,
		},
		{
			title: '年级',
			dataIndex: 'nj',
			key: 'nj',
			...getColumnSearchProps('nj'),
			with: 46,
			// sorter: (a, b) => a.nj - b.nj,
		},
		{
			title: '姓名',
			key: 'person_name',
			dataIndex: 'person_name',
			with: 46,
		},
		{
			title: '在校状态',
			dataIndex: 'zxsztmc',
			...getColumnSearchProps('zxsztmc'),
			key: 'zxsztmc',
			// sorter: (a, b) =>
			// 	a.zxsztmc.localeCompare(b.zxsztmc, 'zh-Hans-CN', { sensitivity: 'accent' }),
			with: 76,
		},
		{
			title: '抓拍数量',
			dataIndex: 'snap_sums',
			ellipsis: true,
			key: 'snap_sums',
			// sorter: (a, b) => a.snap_sums - b.snap_sums,
		},
		{
			title: '点名次数',
			dataIndex: 'check_sums',
			ellipsis: true,
			key: 'check_sums',
			// sorter: (a, b) => a.check_sums - b.check_sums,
		},
  ];
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
		});
	};
	const getList = async () => {
		setLoading(true);
    let params = {
			pageNumber: pagina.current,
			pageSize: pagina.pageSize,
      ...searchObject,
		}
   
		let { success, value } = await getStudentAttDetailSts(params).finally(() => {
			setLoading(false);
		});
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
					showTotal: total => `总共 ${total} 条`,
				}}
				onChange={onChange}
			/>
		</div>
	);
};

export default Details;
