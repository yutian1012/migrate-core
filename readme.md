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

//格式化数据模型--格式化发生在处理目标数据时
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

7）记录信息--使用jpa方式实现

create database if not exists `migrate` default charset utf8 collate utf8_general_ci;

记录正确处理的数据，错误无法处理的数据。并提供一个相应的统计功能。--日志模型LogModel
再处理前，需要将处理相关的结构数据入库保存，方便查看每次正确处理的信息管理的数据结构。一般针对表结构进行单独存储。--tableModel）
如，设置了一个表的处理相关的配置，那么针对该设置处理的记录应该生成相应的记录（LogModel数据）。这些记录中包括正确信息和错误信息（status）。以及相应的统计信息。

一个批次中可以处理多张表，查看该处理批次时，能够看到该批次处理的表，并对每个处理的表进行详细的查看。

提炼领域对象

表设置信息TableModel，需要对上面的信息进行扩展，相关的字段信息可存储成json信息，直接保存到TableModel字段中。（即只有TableModel需要序列化到数据库中，其他的字段如FieldModel，WhereModel等无须序列化到数据库表中，只需将相应的json数据保存到TableModel对应的序列化记录的字段中）。

批次信息BatchModel，批次ID,父批次ID，批次名称，批次表集合（子表信息），批次创建时间（是否正确处理完了该批次，默认为false，处理完成后更新该字段），处理量，成功量

注：子批次只针对单张表进行再出来

执行一个批次，需要记录这个批次的执行日志，查看批次的执行日志等信息。
BatchLogModel记录批次执行日志，查看执行日志时显示的是批次执行记录，即某一时刻执行的批次记录，查看批次执行记录出现批次中每个表的一个详细执行列表。包括统计信息等。
BatchLogModel对象包括id，批次名称，批次执行日期，批次执行量，成功数量

子执行批次，是在查看表的详细执行记录列表时，该页面提供了一个继续执行修正错误的按钮。点击该按钮，会对执行错误的记录进行从新执行，会跳过正确执行完成的记录。

日志记录对象LogModel，日志id（id），业务数据主键（DataId，针对业务表中存在有主键的数据），处理情况（status），关键展示数据（dealData varchar），表配置信息（tableId），处理表名（tableName，冗余字段展示使用）,消息信息（message）,batchId

8）错误信息处理思路

第一次处理可能会存在各种意想不到的错误，记录这些错误，错误信息，错误相关的记录信息（一般是主键，LogModel的dataId字段）。
提供一个错误处理功能，针对错误进行再处理功能。对错误信息能够进行再次处理的能力（这时正确的数据不需要再处理了）。再处理时可以设置相关的处理类，可以是一个链式的处理方式（即指定多个处理类，按顺序执行这些处理类）。

处理模型HandlerModel：ID，handlerClass，script（相关的脚步，支持动态的传入），type（处理类型，使用处理类或者使用groovy脚步，内置脚本引擎）

9）设置多数据源问题

多数据源先在配置文件application.properties中设置，思考如何实现动态设置数据源，并进行相应的匹配和切换。

使用一个配置类，来配置datasource以及与之相关的jdbcTemplate对象。从而注入到dao层

10）测试更新数据

数据库test1
create database if not exists `test1` default charset utf8 collate utf8_general_ci;
原表及数据，
DROP TABLE IF EXISTS `migratesource`;
create table migratesource(
	id bigint(20) not null primary key,
	name varchar(32) not null comment '用户名',
	email varchar(32) not null comment '邮箱',
	createtime date not null
);
insert into migratesource(id,name,email,createtime) values(1,'zhangsan1','zhangsan1@qq.com','2018-05-05');
insert into migratesource(id,name,email,createtime) values(2,'zhangsan2','zhangsan2@qq.com','2018-05-05');
insert into migratesource(id,name,email,createtime) values(3,'zhangsan3','zhangsan3@qq.com','2018-05-05');
insert into migratesource(id,name,email,createtime) values(4,'zhangsan4','zhangsan4@qq.com','2018-05-05');
insert into migratesource(id,name,email,createtime) values(5,'zhangsan5','zhangsan5@qq.com','2018-05-05');

数据库test2
create database if not exists `test2` default charset utf8 collate utf8_general_ci;
目标表及数据(新应用的数据表字段与原数据表是不同的)，
DROP TABLE IF EXISTS `migratedest`;
create table migratedest(
	id bigint(20) not null primary key,
	username varchar(32) not null comment '用户名',
	useremail varchar(32) comment '邮箱',
	createtime date not null
);
insert into migratedest(id,username,useremail,createtime) values(1,'zhangsan1','','2018-05-05');
insert into migratedest(id,username,useremail,createtime) values(2,'zhangsan2','','2018-05-05');
insert into migratedest(id,username,useremail,createtime) values(3,'zhangsan3','','2018-05-05');

实现数据的更新，将migratesource表中email信息更新到目标表migratedest的useremail字段中。两表的关联字段：migratesource表使用name字段，migratedest使用username字段进行关联更新。

应用中数据源配置在application.properties文件中

数据源配置类查看com.ipph.migratecore.config.DataSourceConfig类

在xml中配置更新信息
<table type="UPDATE" to="migratedest" from="migratesource">
	<fields>
		<!-- 目标表要更新的字段 -->
		<field name="useremail" applyType="TARGET" valueType="FIELD" value="email"/>
		<!-- 源表要查询获取的字段数据 -->
		<field name="name" applyType="SOURCE"/>
		<field name="email" applyType="SOURCE"/>
	</fields>
	<where>
		<!-- 目标表更新数据的条件，migratesource表使用name字段，migratedest使用username字段进行关联更新  -->
		<condition type="EQUAL" applyType="TARGET">
			<field name="username" applyType="TARGET" valueType="FIELD" value="name"/>
		</condition>
		<!-- 源表获取待更新的数据集条件，这里设置id值<=3 -->
		<condition type="LE" applyType="SOURCE">
			<field name="id" applyType="SOURCE" valueType="FIXED" value="3" />
		</condition>
	</where>
</table>

update更新数据流程：
获取待更新的数据，判断目标表中待更新的记录是否存在，如果不存在则抛出异常并记录，如果存在则进行更新

insert迁移数据流程
获取待迁移的数据，判断目标表汇总该记录是否已经存在（已经迁移过了，或者唯一键问题），如果存在则给出提示，如果不存在则执行插入操作

11）在应用中添加缓存，表结构信息的缓存--防止每次处理都从新创建相应的sql，where字段等。

12）通过上传xml文档获取table的表配置信息

13）添加网页模板

14) 记录日志执行过程

15）专利数据的更新操作

16）进度查看

17）动态数据源配置，装载和卸载

18）使用消息实现异步操作，或使用线程实现异步

19）清空数据表结构
-- DROP TABLE IF EXISTS `batch_model`;
-- DROP TABLE IF EXISTS `batch_table_model`;
-- DROP TABLE IF EXISTS `table_model`; 
DROP TABLE IF EXISTS `log_model`;

20）查看原表信息

21）根据查询条件查询数据

22）对日志信息进行错误分类
操作成功，数据格式化错误，数据未检索到

23)导出excel大数据量问题

24）分页逻辑的处理

25）日志查询重构

26）表和批次信息的修改和删除等功能

27)统计字段信息

28）formatmodel中提供一个默认值（sourceType,area如果数据为空，则设置一个默认值）

29）迁移数据到子表

30）JsonMethodFormater格式化数据问题
