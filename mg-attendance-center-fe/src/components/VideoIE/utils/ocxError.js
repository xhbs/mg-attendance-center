// 非关键错误,不需要释放通道,重新连接,关键错误需要释放通道,重新连接.

export const IC_SUCCESS = 0		/* 操作成功 */
export const IC_ERROR = -1		// 未知错误

export const IC_ERROR_INITIALIZED = 1		// 已初始化
export const IC_ERROR_UNINITIALIZED = 2		// 未初始化
export const IC_ERROR_INIT_SOCKET_FAILED = 3		// 初始化网络协议栈失败
export const IC_ERROR_MALLOC = 4		// 分配内存出错
export const IC_ERROR_SESSION_HANDLE = 5		// 非法的会话句柄
export const IC_ERROR_PU_HANDLE = 6		// 非法的设备句柄
export const IC_ERROR_RESOURCE_HANDLE = 7		// 非法的资源句柄
export const IC_ERROR_STREAM_HANDLE = 8		// 非法的流句柄
export const IC_ERROR_INVALID_ADDRESS = 9		// 地址格式错误
export const IC_ERROR_PARSER_RESPONSE = 10		// 解析响应报文错误
export const IC_ERROR_RESPONSE_FORMAT = 11		// 响应报文格式错误
export const IC_ERROR_RESOURCE_TYPE = 12		// 错误的资源类型
export const IC_ERROR_BUFFER_LACK = 13		// 缓冲区不足
export const IC_ERROR_INPUT_PARAM = 14		// 错误的输入参数
export const IC_ERROR_CREATE_DATA_CHANNEL = 15		// 创建数据通道失败
export const IC_ERROR_CREATE_NCS = 16		// 创建NCS模块失败
export const IC_ERROR_CREATE_DCS = 17		// 创建DCS模块失败
export const IC_ERROR_SERVER_STATUS = 18		// 服务器状态错误
export const IC_ERROR_REPORT_FORMAT = 19		// 上报报文格式错误
export const IC_ERROR_UNSUPPORT_REQ = 20		// 不支持的请求
export const IC_ERROR_DOMAINNAME_TO_IP = 21		// 域名解析失败
export const IC_ERROR_ADD_RESOURCE = 22		// 创建资源失败

/* 云台锁定错误码 */
export const IC_ERROR_VERIFY_PTZLOCKED = 51   // 云台已锁定，请先解锁
export const IC_ERROR_VERIFY_UNLOCKPTZPRIFAILED = 52   // 用户优先级不足，不能解锁云台
export const IC_ERROR_VERIFY_PREPOINTREFED = 53   // 预置点被引用

// 前端设备返回的错误码
export const IC_ERROR_OUT_OF_RANGE = 1001	// 设置值不在取值范围内(设置配置的时候返回)
export const IC_ERROR_UNSUPPORT = 1002	// 操作不支持(设置配置,获取配置或控制命令的时候返回)
export const IC_ERROR_INVALID_PARAM = 1003	// 参数错误(控制命令的时候返回)
export const IC_ERROR_INVALID_FORMAT = 1004	// 报文格式错误(设置配置和控制命令的时候返回)
export const IC_ERROR_INVALID_RESOURCE = 1005	// 资源错误(设置配置,获取配置或控制命令的时候返回)
export const IC_ERROR_OPERATE_FAILED = 1006	// 操作失败(设置配置或控制命令的时候返回)
export const IC_ERROR_WITHOUT_PERMISSION = 1007	// 操作没有权限(控制命令的时候返回)
export const IC_ERROR_UNUSABLE = 1008	// 通道不在线(NVR的IPC通道不在线时,所以操作都返回这个值)
export const IC_ERROR_OBJECT_EXIST = 1009	// 通道已有码流在播放
export const IC_ERROR_TIME_OUT = 1010	// 功能超时(转发命令给功能程序，等待响应超时时，返回这个)
export const IC_ERROR_ROUTE_MODE = 1501	// 路由器模式导致申请流失败
export const IC_ERROR_STREAM_DISABLE = 1502	// 该流被禁用导致申请流失败
export const IC_ERROR_FLOW_LIMIT = 1503	// 流量限制导致申请流失败
export const IC_ERROR_TIME_LIMIT = 1504	// 上网时长限制导致申请流失败
export const IC_ERROR_NO_RULE = 1505	// 命令没有转发关系

// 服务返回的错误码
export const IC_ERROR_FORMAT = 2001	// 错误的消息体格式
export const IC_ERROR_PARAM = 2002	// 错误的参数（属性值）
export const IC_ERROR_UNSUPPORTOPERATION = 2003	// 不支持的操作
export const IC_ERROR_DESTINATION = 2004	// 目标鉴权失败
export const IC_ERROR_PRIORITY = 2005	// 优先级鉴权失败
export const IC_ERROR_EPID = 2006	// EPID鉴权失败
export const IC_ERROR_NOOPTPERMISSION = 2007	// 操作鉴权失败
export const IC_ERROR_NORESPERMISSION = 2008	// 资源鉴权失败
export const IC_ERROR_TIMEOUT = 2009	// 命令超时
export const IC_ERROR_ROUTEFAILED = 2010	// 路由失败
export const IC_ERROR_NOOBJPERMISSION = 2011	// 没有对象操作权限
export const IC_ERROR_OBJNOTEXIST = 2012	// 对象不存在
export const IC_ERROR_OBJALREADYEXIST = 2013	// 对象已存在
export const IC_ERROR_USERFULL = 2014	// 超过支持的最大用户数
export const IC_ERROR_USEROPTOVERFLOW = 2015	// 目标用户支持的操作集过大
export const IC_ERROR_LOWPRIORITY = 2016	// 优先级不足
export const IC_ERROR_TOKENNOTEXSIT = 2017	// 请求的令牌不存在
export const IC_ERROR_NOVALIDDISPATCHER = 2018	// 没有可用的分发单元
export const IC_ERROR_AUDIO_CHANNEL_OCCUPY = 2019	// 音频输出通道已被占用
export const IC_ERROR_INVALIDRES = 2020	// 非法的资源
export const IC_ERROR_STREAMOVERLOADED = 2021	// 超过最大的流转发负荷
export const IC_ERROR_NOVALIDOSS = 2023	// 没有可用的云存储服务
export const IC_ERROR_NORECORD = 2024	// 没有可点播录像
export const IC_ERROR_INVALIDVODID = 2025	// 非法的点播ID

// 用户相关错误码
/* 用户下线原因 */
export const IC_ERROR_KICKUSER_DELETEEP = 2100    // 删除企业，将企业下所有在线用户踢下线
export const IC_ERROR_KICKUSER_DELETEORG = 2101    // 删除组织，将组织下所有在线用户踢下线
export const IC_ERROR_KICKUSER_DELETEUSER = 2102    // 删除用户，将用户踢下线
export const IC_ERROR_KICKUSER_ACTOREXPIRED = 2103    // 角色过期，将角色关联的所有在线用户踢下线
export const IC_ERROR_KICKUSER_CLIENT = 2104    // 高优先级用户踢其他用户
export const IC_ERROR_KICKUSER_DELETEACTOR = 2105    // 删除角色，将角色关联的所有在线用户踢下线
export const IC_ERROR_KICKUSER_DISABLE = 2106    // 用户被禁用，将所有此账号的用户踢下线
export const IC_ERROR_KICKUSER_TIMEOUT = 2107    // 较长时间未做操作，踢下线
export const IC_ERROR_KICKUSER_PSWCHANGES = 2108    // 密码修改，踢下线

/* 用户登录错误码 */
export const IC_ERROR_VERIFY_USERNOTEXIST = 2201	// 用户不存在
export const IC_ERROR_VERIFY_USERINACTIVE = 2202	// 用户被禁用
export const IC_ERROR_VERIFY_EPIDINACTIVE = 2203	// EPID被禁用
export const IC_ERROR_VERIFY_PASSWORDWRONG = 2206	// 密码错误
export const IC_ERROR_VERIFY_USBKEYINVALID = 2207	// 无效的USBKey
export const IC_ERROR_VERIFY_USERFULL = 2208	// 用户已满
export const IC_ERROR_VERIFY_TIMEOUT = 2209	// 认证超时
export const IC_ERROR_VERIFY_ROUTEFAILED = 2210	// 路由失败
export const IC_ERROR_VERIFY_AUTHKEYINVALID = 2211	// 无效的AuthKey
export const IC_ERROR_VERIFY_EPONLINEUSERFULL = 2212	// 超过企业限制的最大在线用户数
export const IC_ERROR_VERIFY_USERNOTREUSE = 2213	// 用户不能复用
export const IC_ERROR_VERIFY_USERACTOREXPIRED = 2214	// 用户角色过期
export const IC_ERROR_VERIFY_USERTHIRDPARTY = 2254	// 第三方用户，不允许登录

// 命令通道返回错误码
export const IC_ERROR_MC_WOULDBLOCK = 3001	// 操作正在进行中
export const IC_ERROR_MC_PARAM = 3002	// 函数参数错误
export const IC_ERROR_REQTIMEOUT = 3003	// 请求超时
export const IC_ERROR_STATUS = 3004	// 状态错误
export const IC_ERROR_NOEXTPARAM = 3005	// 无扩展参数
export const IC_ERROR_UNKOWN = -3001	// 未定义错误,一般不会返回这个
export const IC_ERROR_SOCKETCONNECT = -3002	// SOCKET连接错误
export const IC_ERROR_CONTIMEOUT = -3003	// 连接超时
export const IC_ERROR_MC_MALLOC = -3004	// 内存分配出错
export const IC_ERROR_SENDDATA = -3005	// 发送数据时出错
export const IC_ERROR_RECVDATA = -3006	// 接收数据时出错
export const IC_ERROR_STOP = -3007	// 操作被中止
export const IC_ERROR_NOROUTE	= -3008	// 路由失败
export const IC_ERROR_NOSERVER = -3009	// 服务未开启
export const IC_ERROR_UNREACHABLE	= -3010	// 地址不可达
export const IC_ERROR_EXTPARAMLEN = -3104	// 注册时返回的扩展参数长度错误
export const IC_ERROR_HTTPHEADNOCONTENT = -3108	// 注册时返回HTTP头中无Content-Length
export const IC_ERROR_HTTPHEADOVERFLOW = -3109	// 注册时返回HTTP头中的Content-Length溢出
export const IC_ERROR_HTTPCONTENTNOXMLHEAD = -3110	// 注册时返回HTTP内容中没有XML头
export const IC_ERROR_HTTPCONTENTXMLFORMAT = -3111	// 注册时返回HTTP内容中XML格式错误
export const IC_ERROR_REDIRECTRESULT = -3112	// 注册时返回的重定向报文中的结果错误
export const IC_ERROR_REDIRECTVERSION = -3113	// 注册时返回的挑战报文版本号不对
export const IC_ERROR_SYNCFLAG = -3201	// 接收命令时起始字符
export const IC_ERROR_VERSION = -3202	// 接收命令时版本错误
export const IC_ERROR_CTRLFLAG = -3203	// 接收命令时控制字段错误
export const IC_ERROR_MSGTYPE	= -3204	// 接收命令时消息类型错误
export const IC_ERROR_PACKETLEN	= -3205	// 接收命令时包长度错误
export const IC_ERROR_PACKETTYPE = -3206	// 接收命令时包类型错误
export const IC_ERROR_BODYLEN	= -3207	// 接收命令时包体长度非8字节整数倍
export const IC_ERROR_REGISTER_UNKOWN = -3299	// 注册时返回未知错误,-1和其他不在范围内的值
export const IC_ERROR_REDIRECT_UNKOWN	= -3499	// 重定向时返回未知错误,-1和其他不在范围内的值
export const IC_ERROR_MC_FORMAT = -3501	// 报文格式出错
export const IC_ERROR_UNSUPPORT_TERMINAL_TYPE = -3502	// 不支持的终端类型
export const IC_ERROR_NONE_ONLINE = -3503	// 没有在线的PUI或CUI
export const IC_ERROR_NONE_SUPPORT_REDIRECT = -3504	// 没有支持重定向的CUI
export const IC_ERROR_UNSUPPORT_PRUDUCER_ID	= -3505	// 不支持该厂商ID

// 数据通道返回错误码
export const IC_ERROR_DC_WOULDBLOCK	= 4001	// 操作暂时无法完成
export const IC_ERROR_FRAMEBUF_LEN = 4002	// 帧长度错误
export const IC_ERROR_END	= 4003	// 接收到结束包,比如下载完成等
export const IC_ERROR_UNKNOW = -4001	// 未定义错误,一般不会返回这个
export const IC_ERROR_DC_PARAM = -4002	// 参数错误
export const IC_ERROR_IVALHANDLE = -4003	// 无效句柄
export const IC_ERROR_DC_MALLOC = -4004	// 内存错误
export const IC_ERROR_DC_TIMEOUT = -4005	// 超时
export const IC_ERROR_CONNECT = -4006	// 连接错误
export const IC_ERROR_TCPSEND	= -4007	// TCP发送出错
export const IC_ERROR_TCPRECV = -4008	// TCP接收出错
export const IC_ERROR_UDP = -4010	// UDP网络出错
export const IC_ERROR_DISCARD	= -4011	// 收到断开连接的命令
export const IC_ERROR_DC_UNSUPPORT = -4012	// 操作不支持
export const IC_ERROR_NOTINIT = -4013	// 没有初始化
export const IC_ERROR_INVTOKEN = -4101	// 无效令牌 
export const IC_ERROR_CHTYPEERR = -4102	// 通道类型错误
export const IC_ERROR_CHEXIST = -4103	// 通道已经存在
export const IC_ERROR_PACKET_VER = -4201	// 包版本错误
export const IC_ERROR_PACKET_TYPE	= -4202	// 包类型错误
export const IC_ERROR_PACKET_LEN = -4203	// 包长度错误
export const IC_ERROR_PACKET_BFLAG = -4204	// 帧开始标志错误
export const IC_ERROR_PACKET_EFLAG = -4205	// 帧结束标志错误
export const IC_ERROR_FRAME_TYPE = -4211	// 帧类型错误
export const IC_ERROR_FRAME_LEN	 = -4212	// 数据帧长度错误


//TVWALL相关错误码
export const IC_ERROR_NO_DECODER_CHANNAL = 0x4000	// 解码通道不存在
export const IC_ERROR_NO_FIND_WALL = 0x4001	// 电视墙配置不存在
export const IC_ERROR_NO_FIND_PLAN = 0x4002	// 电视墙预案不存在
export const IC_ERROR_DBS_ERROR	 = 0x4003	// 数据库操作错误
export const IC_ERROR_NO_RECORDER	= 0x4004	// 录像点播错误
export const IC_ERROR_NO_IPC = 0x4005	// 编码通道不存在
export const IC_ERROR_DECODER_CHANNEL_WRONG = 0x4006
