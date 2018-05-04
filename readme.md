1) xml文件校验存在错误
2) xml节点处理的封装--策略类

3) 从源表update数据到目标表

获取原表的待更新字段，如申请号。下面的sql中汉字的地方都是可以传入参数的
select 字段	from	数据源+源表  	where	条件

更新操作，
update 目标表 	set	字段	where 条件

结构规划

源表：设置获取的字段，设置条件（条件一般为固定值）

目标表：设置更新的字段（可以为固定值，也可以从源表获取的字段中进行选择），设置条件（可以为规定值，也可以从源表获取的字段中进行选择）

实体类分析：

tableModel成员：源表和目标表（字符串，from,to），迁移类型（update更新，枚举实现），字段集合（获取的源字段集合和更新的目标字段集合 fieldModel），条件(whereModel)，格式化字段集合（formatFieldList）

FieldModel成员：字段类型（fieldType）,字段名称（name），字段应用类型（applyType，表明是源表还是目标表），字段值来源（valueType,固定值还是其他方式获取），value(固定值)

whereModel成员：条件集合（ConditionList）

//<codition type="" applyType><field></field></condition>
ConditionModel成员：类型（等于，不等于。。。每个类型都有相应的处理类），field字段（FieldModel），字段应用类型（applyType，表明是源表还是目标表）

//格式化数据模型
FormatModel成员：格式化处理类（formatClass）,格式化处理参数，格式化处理字段（fieldName）

4) 从源表迁移数据到目标表

获取原表的待更新字段，如申请号。下面的sql中汉字的地方都是可以传入参数的
select 字段	from	数据源+源表  	where	条件

插入数据到目标表
insert into 目标表		字段集合	value	字段值

5）迁移子表的处理

应用场景：主表中一个字段信息如申请人，是多值的情况（值与值之间使用分号或逗号分隔的场景），迁移时，由于目标应用处理数据时是将其分解为子表进行处理的。因此，在迁移数据时，这部分数据需要拆分出来存储到子表中。甚至有相反的操作

首先子表也是表，需要继承tableModel

subtableModel成员:

6）结合流程实现自定义流程处理