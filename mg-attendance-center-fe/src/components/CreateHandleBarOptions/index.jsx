/*
 * @Author: xiedan WX042
 * @since: 2020-08-27 08:31:19
 * @lastTime: 2020-12-18 10:09:56
 * @FilePath: \biz-scene-attendance-web\src\components\CreateHandleBarOptions\index.jsx
 * @Description: 表格操作栏 操作栏包含：新增、删除、导出、状态更改、自定义内容等等
 */

export const HandleBarOptions = (obj) => {
  const {
    handleAdd,
    handleDel,
    handleExport,
    handleStatusChange, // 改变状态
    customizeBtn = [],  // 自定义添加按钮
    customizeSearch = null, // 自定义右侧搜索组件
    disabledAdd = false,
    disabledDel = false,
    disabledExp = false,
    disabledStu = false,
    loadingExp = false,
    searchPlaceholder = '检索内容',
    searchStyle = {},
  } = obj;

  const Add = handleAdd ? [{
    antdProps: {
      icon: "hz-add",
      children: "新增",
      disabled: disabledAdd,
      onClick: handleAdd,
    },
  }] : [];

  const Delete = handleDel ? [{
    antdProps: {
      icon: "hz-delete",
      children: "删除",
      disabled: disabledDel,
      onClick: () => { handleDel() },
    },
  }] : [];

  const Export = handleExport ? [{
    antdProps: {
      icon: 'hz-export',
      children: '导出',
      disabled: disabledExp,
      onClick: handleExport,
      loading: loadingExp,
      title: "最大导出数量为 10万 条数据",
    },
  }] : [];

  // 状态更改
  const StatusChange = handleStatusChange ? [{
    antdProps: {
      icon: 'hz-edit',
      children: '状态更改',
      disabled: disabledStu,
      onClick: handleStatusChange,
    },
  }] : [];

  return {
    handleOptions: {
      elements: [
        ...Add,
        ...StatusChange,
        ...customizeBtn,
        ...Export,
        ...Delete,
      ],
    },
    searchOptions: customizeSearch ? customizeSearch : {
      show: true, // 是否展示
      searchKey: 'search', // 搜索区参数字段配置
      antdProps: {
        allowClear: true,
        placeholder: searchPlaceholder,
        style: {
          width: 240,
          ...searchStyle,
        },
      },
    },
  }
}
