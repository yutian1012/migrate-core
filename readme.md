1) xml文件校验存在错误

错误信息：cvc-elt.1: Cannot find the declaration of element 'tables'

使用校验工具也会抛出相应的异常：org.xml.sax.SAXParseException; lineNumber: 2; columnNumber: 190; cvc-elt.1: 找不到元素 'tables' 的声明。

未找到原因，不过在编辑xml文档时，能够根据xsd文档提示相应的元素和属性了。

2) xml节点处理的封装

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

7）记录信息

记录正确处理的数据，错误无法处理的数据。并提供一个相应的统计功能。--日志模型LogModel
再处理前，需要将处理相关的结构数据入库保存，方便查看每次正确处理的信息管理的数据结构。一般针对表结构进行单独存储。--tableModel）
如，设置了一个表的处理相关的配置，那么针对该设置处理的记录应该生成相应的记录（LogModel数据）。这些记录中包括正确信息和错误信息（status）。以及相应的统计信息。

一个批次中可以处理多张表，查看该处理批次时，能够看到该批次处理的表，并对每个处理的表进行详细的查看。

提炼领域对象

日志记录对象LogModel，批次ID，父批次ID（再处理），业务数据主键（DataId，针对业务表中存在有主键的数据），处理情况（status），关键展示数据（dealData varchar），表配置信息（tableId），处理表名（tableName）

表设置信息TableModel，需要对上面的信息进行扩展，相关的字段信息可存储成json信息，直接保存到TableModel字段中。（即只有TableModel需要序列化到数据库中，其他的字段如FieldModel，WhereModel等无须序列化到数据库表中，只需将相应的json数据保存到TableModel对应的序列化记录的字段中）。

批次信息BatchModel，批次ID,批次处理时间，批次处理量，成功量，批次状态（是否正确处理完了该批次，默认为false，处理完成后更新该字段）

8）错误信息处理思路

第一次处理可能会存在各种意想不到的错误，记录这些错误，错误信息，错误相关的记录信息（一般是主键，LogModel的dataId字段）。
提供一个错误处理功能，针对错误进行再处理功能。对错误信息能够进行再次处理的能力（这时正确的数据不需要再处理了）。再处理时可以设置相关的处理类，可以是一个链式的处理方式（即指定多个处理类，按顺序执行这些处理类）。

处理模型HandlerModel：ID，handlerClass，script（相关的脚步，支持动态的传入），type（处理类型，使用处理类或者使用groovy脚步，内置脚本引擎）

9）设置多数据源问题

多数据源先在配置文件application.properties中设置，思考如何实现动态设置数据源，并进行相应的匹配和切换。
