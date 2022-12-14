INSERT INTO "REPOSITORY"."PLUGIN" ("PLUGIN_ID", "NAME", "BUILD", "VERSION", "DESCRIPTION", "COMPATIBLE_VERSION", "OS", "ARCH", "DEPENDENCY", "CATEGORY_ID", "PUBLISHED", "DELETED")
VALUES
('e93fceffb75841e587b6dabb7592e81d', '标绘布局插件1', '2022-07-02', '1.0.0', '标绘布局插件1', '1.0.0', 'windows', 'x86', null, '01', 0, 0),
('378792d7b75c4cbe8969a5008db2ccf2', '第三方开发包1', '2022-07-02', '1.0.0', '第三方开发包1', '1.0.0', 'kilin linux', 'x86', 'e93fceffb75841e587b6dabb7592e81d', '02', 0, 0),
('9501e5e1c33c45c1924108fa0d0fd4c0', '矢量切图插件2', '2022-07-02', '1.0.0', '矢量切图插件2', '1.0.0', 'kilin linux', 'x86', 'e93fceffb75841e587b6dabb7592e81d', '03', 0, 0),
('cb0ace146ac540e3bdc361ae85c64c33', '地图显示插件3', '2022-07-02', '1.0.0', '地图显示插件3', '1.0.0', 'kilin linux', 'x86', '378792d7b75c4cbe8969a5008db2ccf2', '04', 0, 0),
('edbb68e3cae34e4390c063a940e24c37', '三维组件开发包4', '2022-07-02', '1.0.0', '三维组件开发包4', '1.0.0', 'kilin linux', 'x86', '378792d7b75c4cbe8969a5008db2ccf2', '05', 0, 0),
('a0e406912ba14ec2ad6f2025b8eb3777', '导航插件5', '2022-07-02', '1.0.0', '导航插件5', '1.0.0', 'kilin linux', 'x86', '9501e5e1c33c45c1924108fa0d0fd4c0', '06', 0, 0),
('6e97fb506a72454f89c8f5f77648dc8d', '地图更新插件6', '2022-07-02', '1.0.0', '地图更新插件6', '1.0.0', 'kilin linux', 'x86', '378792d7b75c4cbe8969a5008db2ccf2', '07', 0, 0),
('ea9a7db8558a4478a00f2fd117155b9d', '高程显示插件7', '2022-07-02', '1.0.0', '高程显示插件7', '1.0.0', 'kilin linux', 'x86', '6e97fb506a72454f89c8f5f77648dc8d', '08', 0, 0),
('0925a5c54e044b5581dbfd39d5117f0d', '卫星影像插件8', '2022-07-02', '1.0.0', '卫星影像插件8', '1.0.0', 'kilin linux', 'x86', null, '09', 0, 0),
('8fb9127744074611b51cecdaa51a5550', '动态分析插件9', '2022-07-02', '1.0.0', '动态分析插件9', '1.0.0', 'kilin linux', 'aarch64', '0925a5c54e044b5581dbfd39d5117f0d', '10', 0, 0),
('12d9a2ccc69b4d5d92d7e27117cbf41f', '算法插件10', '2022-07-02', '1.0.0', '算法插件10', '1.0.0', 'kilin linux', 'aarch64', '0925a5c54e044b5581dbfd39d5117f0d', '11', 0, 0),
('772551d7b16b4f3b9b4e3daa96e1aa39', '智能语言插件11', '2022-07-02', '1.0.0', '智能语言插件11', '1.0.0', 'kilin linux', 'aarch64', '0925a5c54e044b5581dbfd39d5117f0d', '12', 0, 0),
('1400ee5c2aa7486d909d2a8c90be2ce8', '演播插件12', '2022-07-02', '1.0.0', '演播插件12', '1.0.0', 'kilin linux', 'aarch64', '0925a5c54e044b5581dbfd39d5117f0d', '13', 0, 0),
('da8f0fdb56934e128289624d6bbab081', '倾斜摄影插件13', '2022-07-02', '1.0.0', '倾斜摄影插件13', '1.0.0', 'windows', 'aarch64', '12d9a2ccc69b4d5d92d7e27117cbf41f', '14', 0, 0),
('59ac90212c494c549bef52b91069326d', '全球地名库插件14', '2022-07-02', '1.0.0', '全球地名库插件14', '1.0.0', 'windows', 'aarch64', '12d9a2ccc69b4d5d92d7e27117cbf41f', '15', 0, 0),
('fad8e01227804bd286b36f6207918436', '态势分析插件15', '2022-07-02', '1.0.0', '态势分析插件15', '1.0.0', 'windows', 'aarch64', '772551d7b16b4f3b9b4e3daa96e1aa39', '16', 0, 0),
('7396981489804eedb676240a58cf21fa', '无敌插件666', '2022-07-02', '1.0.0', '无敌插件666', '1.0.0', 'windows', 'aarch64', '1400ee5c2aa7486d909d2a8c90be2ce8', '17', 0, 0);

commit;

INSERT INTO "REPOSITORY"."CATEGORY" ("CATEGORY_ID", "NAME", "PARENT_ID")
VALUES
('01', '标绘', null),
('02', '第三方', '01'),
('03', '态势', '01'),
('04', '算法', '02'),
('05', '倾斜', '02'),
('06', '地名', '03'),
('07', '智能', '04'),
('08', '分析', null),
('09', '演播', '08'),
('10', '地图', '08'),
('11', '无敌', '09'),
('12', '矢量', '09'),
('13', '栅格', '02'),
('14', '高程', '02'),
('15', '3D', '02'),
('16', '动态', '03'),
('17', '导航', '03');

commit;